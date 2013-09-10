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

import com.dbay.apns4j.tools.ApnsTools;

public class PushNotification {
	private int id;
	private int expire;
	private String token;
	private Payload payload;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getExpire() {
		return expire;
	}
	public void setExpire(int expire) {
		this.expire = expire;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public Payload getPayload() {
		return payload;
	}
	public void setPayload(Payload payload) {
		this.payload = payload;
	}
	/**
	 * We need to check the size of payload. 
	 * After checking, reuse it in order to avoid calculate bytes array twice although it seems not perfect
	 * @param payloads
	 * @return
	 */
	public byte[] generateData(byte[] payloads) {
		byte[] tokens = ApnsTools.decodeHex(getToken());
		return ApnsTools.generateData(getId(), getExpire(), tokens, payloads);
	}
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("id="); sb.append(getId());
		sb.append(" token="); sb.append(getToken());
		sb.append(" payload="); sb.append(getPayload().toString());
		return sb.toString();
	}
}
