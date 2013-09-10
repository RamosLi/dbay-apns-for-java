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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.json.simple.JSONObject;

/**
 * @author RamosLi
 *
 */
public class Payload {
	private static final String APS = "aps";
	private Map<String, Object> params;
	private String alert;
	private Integer badge;
	private String sound = "";
	
	public Map<String, Object> getParams() {
		return params;
	}
	public void setParams(Map<String, Object> params) {
		this.params = params;
	}
	public void addParam(String key, Object obj) {
		if (params == null) {
			params = new HashMap<String, Object>();
		}
		if (APS.equalsIgnoreCase(key)) {
			throw new IllegalArgumentException("the key can't be aps");
		}
		params.put(key, obj);
	}
	
	public String getAlert() {
		return alert;
	}
	public void setAlert(String alert) {
		this.alert = alert;
	}
	public Integer getBadge() {
		return badge;
	}
	public void setBadge(Integer badge) {
		this.badge = badge;
	}
	public String getSound() {
		return sound;
	}
	public void setSound(String sound) {
		this.sound = sound;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public String toString() {
		JSONObject object = new JSONObject();
		JSONObject apsObj = new JSONObject();
		if (getAlert() != null) {
			apsObj.put("alert", getAlert());
		}
		if (getBadge() != null) {
			apsObj.put("badge", getBadge().intValue());
		}
		if (getSound() != null) {
			apsObj.put("sound", getSound());
		}
		object.put(APS, apsObj);
		if (getParams() != null) {
			for (Entry<String, Object> e : getParams().entrySet()) {
				object.put(e.getKey(), e.getValue());
			}
		}
		return object.toString();
	}
	public static void main(String[] args) {
		Payload payload = new Payload();
		payload.setAlert("How are you?");
		payload.setBadge(1);
		payload.setSound("a");
		payload.addParam("para1", "1231dfasfwer");
		payload.addParam("number", 12312312312L);
		System.out.println(payload.toString());
	}
}
