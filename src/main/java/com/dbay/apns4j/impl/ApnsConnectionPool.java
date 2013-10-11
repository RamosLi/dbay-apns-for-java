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

import java.io.Closeable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.net.SocketFactory;

import com.dbay.apns4j.IApnsConnection;
import com.dbay.apns4j.model.ApnsConfig;

import static com.dbay.apns4j.model.ApnsConstants.*;

/**
 * 
 * @author RamosLi
 *
 */
public class ApnsConnectionPool implements Closeable {
	private static int CONN_ID_SEQ = 1;
	private SocketFactory factory;
	private BlockingQueue<IApnsConnection> connQueue = null;
	
	private ApnsConnectionPool(ApnsConfig config, SocketFactory factory) {
		this.factory = factory;
		
		String host = HOST_PRODUCTION_ENV;
		int port = PORT_PRODUCTION_ENV;
		if (config.isDevEnv()) {
			host = HOST_DEVELOPMENT_ENV;
			port = PORT_DEVELOPMENT_ENV;
		}
		
		int poolSize = config.getPoolSize();
		connQueue = new LinkedBlockingQueue<IApnsConnection>(poolSize);
		
		for (int i = 0; i < poolSize; i++) {
			String connName = (config.isDevEnv() ? "dev-" : "pro-") + CONN_ID_SEQ++;
			IApnsConnection conn = new ApnsConnectionImpl(this.factory, host, port, config.getRetries(), 
					config.getCacheLength(), config.getName(), connName, config.getIntervalTime(), config.getTimeout());
			connQueue.add(conn);
		}
	}
	
	public IApnsConnection borrowConn() {
		try {
			return connQueue.take();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void returnConn(IApnsConnection conn) {
		if (conn != null) {
			connQueue.add(conn);
		}
	}
	
	@Override
	public void close() {
		while (!connQueue.isEmpty()) {
			try {
				connQueue.take().close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * create instance
	 * @param config
	 * @return
	 */
	public static ApnsConnectionPool newConnPool(ApnsConfig config, SocketFactory factory) {
		return new ApnsConnectionPool(config, factory);
	}
}
