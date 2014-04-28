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

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import com.dbay.apns4j.tools.ApnsTools;

public class PushNotification {
	/*
	 * The notification’s priority. Provide one of the following values:
    	(1) 10    The push message is sent immediately.
    	The push notification must trigger an alert, sound, or badge on the device. 
    	It is an error to use this priority for a push that contains only the content-available key.
    	
    	(2)5    The push message is sent at a time that conserves power on the device receiving it.
	 */
	public static final int PRIORITY_SENT_IMMEDIATELY = 10;
	public static final int PRIORITY_SENT_A_TIME = 5;
	
	private static final Pattern pattern = Pattern.compile("[ -]");
	
	private int id;
	private int expire;
	private String token;
	private Payload payload;
	private int priority = PRIORITY_SENT_IMMEDIATELY;
	
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
		this.token = pattern.matcher(token).replaceAll("");
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
		List<FrameItem> list = new LinkedList<FrameItem>();
		list.add(new FrameItem(FrameItem.ITEM_ID_DEVICE_TOKEN, tokens));
		list.add(new FrameItem(FrameItem.ITEM_ID_PAYLOAD, payloads));
		list.add(new FrameItem(FrameItem.ITEM_ID_NOTIFICATION_IDENTIFIER, ApnsTools.intToBytes(getId(), 4)));
		list.add(new FrameItem(FrameItem.ITEM_ID_EXPIRATION_DATE, ApnsTools.intToBytes(getExpire(), 4)));
		list.add(new FrameItem(FrameItem.ITEM_ID_PRIORITY, ApnsTools.intToBytes(getPriority(), 1)));
//		return ApnsTools.generateData(getId(), getExpire(), tokens, payloads);
		return ApnsTools.generateData(list);
	}
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("id="); sb.append(getId());
		sb.append(" token="); sb.append(getToken());
		sb.append(" payload="); sb.append(getPayload().toString());
		return sb.toString();
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
}
