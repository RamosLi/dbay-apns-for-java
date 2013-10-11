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
package com.dbay.apns4j.model;

import java.io.InputStream;

/**
 * @author RamosLi
 *
 */
public class ApnsConfig {
	private String name;
	/**
	 * inputstrem of your certificate file
	 */
	private InputStream keyStore;
	
	private String password;
	
	/**
	 * true:  development environment
	 * false: production environment 
	 */
	private boolean isDevEnv = false;
	/**
	 * connection pool size.
	 */
	private int poolSize = 3;
	/**
	 * the length of the notification cache size. 
	 * It's used for resending notifications when an error response detected
	 */
	private int cacheLength = 100;
	/**
	 * max retry times when sending a notification 
	 */
	private int retries = 3;
	
	/**
	 * EN: The APNS gateway has a CONNECTION_IDLE_TIME, with my experience, it's two hours. 
	 *     So if the interval time of two notifications is more than 30 minutes(default), create new socket.
	 * CN: 根据哥的经验，APNS服务器连接的idle时间为两小时，判断下，如果两条通知的间隔时间超过30分钟，就重新建立连接
	 *     一个连接空闲2小时后，用netstat查看，会处于CLOSE_WAIT状态，但应用层可能并不知道。此时再发通知已经发不出去了，所以需要重连
	 * 
	 * TODO This client will support auto closing connection which is idle for a specific time.
	 */
	private int intervalTime = 30 * 60 * 1000; // 30 minutes
	
	// socket read timeout
	private int timeout = 10 * 1000; // 10 seconds
	
	public InputStream getKeyStore() {
		return keyStore;
	}
	
	public void setKeyStore(String keystore) {
		InputStream is = ApnsConfig.class.getClassLoader().getResourceAsStream(keystore);
		if (is == null) {
			throw new IllegalArgumentException("Keystore file not found. " + keystore);
		}
		setKeyStore(is);
	}
	
	public void setKeyStore(InputStream keyStore) {
		this.keyStore = keyStore;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public boolean isDevEnv() {
		return isDevEnv;
	}
	public void setDevEnv(boolean isDevEnv) {
		this.isDevEnv = isDevEnv;
	}
	public int getPoolSize() {
		return poolSize;
	}
	public void setPoolSize(int poolSize) {
		this.poolSize = poolSize;
	}
	public int getCacheLength() {
		return cacheLength;
	}
	public void setCacheLength(int cacheLength) {
		this.cacheLength = cacheLength;
	}
	public int getRetries() {
		return retries;
	}
	public void setRetries(int retries) {
		this.retries = retries;
	}
	public String getName() {
		if (name == null || "".equals(name.trim())) {
			if (isDevEnv()) {
				return "dev-env";
			} else {
				return "product-env";
			}
		}
		return name;
	}
//	public void setName(String name) {
//		this.name = name;
//	}

	public int getIntervalTime() {
		return intervalTime;
	}

	public void setIntervalTime(int intervalTime) {
		this.intervalTime = intervalTime;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
}
