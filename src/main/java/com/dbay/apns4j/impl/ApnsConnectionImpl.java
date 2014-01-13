/*
 * Copyright 2013 DiscoveryBay Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dbay.apns4j.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

import javax.net.SocketFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import static com.dbay.apns4j.model.ApnsConstants.*;

import com.dbay.apns4j.IApnsConnection;
import com.dbay.apns4j.model.Command;
import com.dbay.apns4j.model.ErrorResponse;
import com.dbay.apns4j.model.Payload;
import com.dbay.apns4j.model.PushNotification;
import com.dbay.apns4j.tools.ApnsTools;

/**
 * @author RamosLi
 *
 */
public class ApnsConnectionImpl implements IApnsConnection {
	
	private static AtomicInteger IDENTIFIER = new AtomicInteger(100);
	
	private Log logger = LogFactory.getLog(ApnsConnectionImpl.class);
	
	// never expire
	private int EXPIRE = Integer.MAX_VALUE;
	
	private SocketFactory factory;
	/**
	 * EN: There are two threads using the socket, one for writing, one for reading.
	 * CN: 一个socket，最多有两个线程在使用它，一个读一个写。
	 */
	private Socket socket;
	/**
	 * When a notification is sent, cache it into this queue. It may be resent.
	 */
	private Queue<PushNotification> notificationCachedQueue = new LinkedList<PushNotification>();
	
	/**
	 * Whether find error in the last connection
	 */
	private boolean errorHappendedLastConn = false;
	
	/**
	 * Whether first write data in the connection
	 */
	private boolean isFirstWrite = false;
	
	private int maxRetries;
	private int maxCacheLength;
	
	private int readTimeOut;
	
	private String host;
	private int port;
	
	/**
	 * You can find the properly ApnsService to resend notifications by this name  
	 */
	private String name;
	
	/**
	 * connection name
	 */
	private String connName;
	private int intervalTime;
	private long lastSuccessfulTime = 0;
	
	private AtomicInteger notificaionSentCount = new AtomicInteger(0);
	
	private Object lock = new Object();
	
	public ApnsConnectionImpl(SocketFactory factory, String host, int port, int maxRetries, 
			int maxCacheLength, String name, String connName, int intervalTime, int timeout) {
		this.factory = factory;
		this.host = host;
		this.port = port;
		this.maxRetries = maxRetries;
		this.maxCacheLength = maxCacheLength;
		this.name = name;
		this.connName = connName;
		this.intervalTime = intervalTime;
		this.readTimeOut = timeout;
	}
	
	@Override
	public void sendNotification(String token, Payload payload) {
		PushNotification notification = new PushNotification();
		notification.setId(IDENTIFIER.incrementAndGet());
		notification.setExpire(EXPIRE);
		notification.setToken(token);
		notification.setPayload(payload);
		sendNotification(notification);
	}
	
	@Override
	public void sendNotification(PushNotification notification) {
		byte[] plBytes = null;
		String payload = notification.getPayload().toString();
		try {
			plBytes = payload.getBytes(CHARSET_ENCODING);
			if (plBytes.length > PAY_LOAD_MAX_LENGTH) {
				logger.error("Payload execeed limit, the maximum size allowed is 256 bytes. " + payload);
				return;
			}
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage(), e);
			return;
		}
		
		/**
		 * EN: If error happened before, just wait until the resending work finishes by another thread
		 *     and close the current socket
		 * CN: 如果发现当前连接有error-response，加锁等待，直到另外一个线程把重发做完后再继续发送  
		 */
		synchronized (lock) {
			if (errorHappendedLastConn) {
				closeSocket(socket);
				socket = null;
			}
			byte[] data = notification.generateData(plBytes);
			boolean isSuccessful = false;
			int retries = 0;
			while (retries < maxRetries) {
				try {
					boolean exceedIntervalTime = lastSuccessfulTime > 0 && (System.currentTimeMillis() - lastSuccessfulTime) > intervalTime;
					if (exceedIntervalTime) {
						closeSocket(socket);
						socket = null;
					}
					
					if (socket == null || socket.isClosed()) {
						socket = createNewSocket();
					}
					
					OutputStream socketOs = socket.getOutputStream();
					socketOs.write(data);
					socketOs.flush();
					isSuccessful = true;
					break;
				} catch (Exception e) {
					logger.error(connName + " " + e.getMessage(), e);
					closeSocket(socket);
					socket = null;
				}
				retries++;
			}
			if (!isSuccessful) {
				logger.error(String.format("%s Notification send failed. %s", connName, notification));
				return;
			} else {
				logger.info(String.format("%s Send success. count: %s, notificaion: %s", connName, 
						notificaionSentCount.incrementAndGet(), notification));
				
				notificationCachedQueue.add(notification);
				lastSuccessfulTime = System.currentTimeMillis();
				
				/** TODO there is a bug, maybe, theoretically.
				 *  CN: 假如我们发了一条错误的通知，然后又发了 maxCacheLength 条正确的通知。这时APNS服务器
				 *      才返回第一条通知的error-response。此时，第一条通知已经从队列移除了。。
				 *      其实后面100条该重发，但却没有。不过这个问题的概率很低，我们还是信任APNS服务器能及时返回
				 */			
				if (notificationCachedQueue.size() > maxCacheLength) {
					notificationCachedQueue.poll();
				}
			}
		}

		if (isFirstWrite) {
			isFirstWrite = false;
			/**
			 * EN: When we create a socket, just a TCP/IP connection created. After we wrote data to the stream,
			 *     the SSL connection had been created. Now, it's time to read data from InputStream
			 * CN: createSocket时只建立了TCP连接，还没有进行SSL认证，第一次写完数据后才真正完成认证。所以此时才开始
			 *     监听InputStream
			 */
			startErrorWorker();
		}
	}
	private Socket createNewSocket() throws IOException, UnknownHostException {
		if (logger.isDebugEnabled()) {
			logger.debug(connName + " create a new socket.");
		}
		isFirstWrite = true;
		errorHappendedLastConn = false;
		Socket socket = factory.createSocket(host, port);
		socket.setSoTimeout(readTimeOut);
		// enable tcp_nodelay, any data will be sent immediately.
		socket.setTcpNoDelay(true);
		
		return socket;
	}
	private void closeSocket(Socket socket) {
		try {
			if (socket != null) {
				socket.close();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	private boolean isSocketAlive(Socket socket) {
		if (socket != null && socket.isConnected()) {
			return true;
		}
		return false;
	}
	
	@Override
	public void close() throws IOException {
		closeSocket(socket);
	}
	
	private void startErrorWorker() {
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				Socket curSocket = socket;
				try {
					if (!isSocketAlive(curSocket)) {
						return;
					}
					InputStream socketIs = curSocket.getInputStream();
					byte[] res = new byte[ERROR_RESPONSE_BYTES_LENGTH];
					int size = -1;
					
					while (true) {
						try {
							size = socketIs.read(res);
							if (size > 0) {
								// break, only when something was read
								break;
							}
						} catch (SocketTimeoutException e) {
							// There is no data. Keep reading.
						}
					}
					
					int command = res[0];
					/** EN: error-response,close the socket and resent notifications
					 *  CN: 一旦遇到错误返回就关闭连接，并且重新发送在它之后发送的通知
					 */			
					if (size == res.length && command == Command.ERROR) {
						int status = res[1];
						int errorId = ApnsTools.parse4ByteInt(res[2], res[3], res[4], res[5]);
						
						if (logger.isInfoEnabled()) {
							logger.info(String.format("%s Received error response. status: %s, id: %s, error-desc: %s", connName, status, errorId, ErrorResponse.desc(status)));
						}
						
						Queue<PushNotification> resentQueue = new LinkedList<PushNotification>();

						synchronized (lock) {
							boolean found = false;
							errorHappendedLastConn = true;
							while (!notificationCachedQueue.isEmpty()) {
								PushNotification pn = notificationCachedQueue.poll();
								if (pn.getId() == errorId) {
									found = true;
								} else {
									/**
									 * https://developer.apple.com/library/ios/documentation/NetworkingInternet/Conceptual/RemoteNotificationsPG/Chapters/CommunicatingWIthAPS.html
									 * As the document said, add the notifications which need be resent to the queue.
									 * Igonre the error one
									 */
									if (found) {
										resentQueue.add(pn);
									}
								}
							}
							if (!found) {
								logger.warn(connName + " Didn't find error-notification in the queue. Maybe it's time to adjust cache length. id: " + errorId);
							}
						}
						// resend notifications
						if (!resentQueue.isEmpty()) {
							ApnsResender.getInstance().resend(name, resentQueue);
						}
					} else {
						// ignore and continue reading
						logger.error(connName + " Unexpected command or size. commend: " + command + " , size: " + size);
					}
				} catch (Exception e) {
					logger.error(connName + " " + e.getMessage(), e);
				} finally {
					/**
					 * EN: close the old socket although it may be closed once before.
					 * CN: 介个连接可能已经被关过一次了，再关一下也无妨，万无一失嘛
					 */
					closeSocket(curSocket);
				}
			}
		});
		
		thread.start();
	}
}
