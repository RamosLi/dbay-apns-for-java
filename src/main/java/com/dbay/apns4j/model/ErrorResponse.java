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

public class ErrorResponse {
	public static final int ERROR_CODE_NO_ERRORS = 0;
	public static final int ERROR_CODE_PROCESSING_ERROR = 1;
	public static final int ERROR_CODE_MISSING_TOKEN = 2;
	public static final int ERROR_CODE_MISSING_TOPIC = 3;
	public static final int ERROR_CODE_MISSING_PAYLOAD = 4;
	public static final int ERROR_CODE_INVALID_TOKEN_SIZE = 5;
	public static final int ERROR_CODE_INVALID_TOPIC_SIZE = 6;
	public static final int ERROR_CODE_INVALID_PAYLOAD_SIZE = 7;
	public static final int ERROR_CODE_INVALID_TOKEN = 8;
	public static final int ERROR_CODE_SHUTDOWN = 10;
	public static final int ERROR_CODE_NONE = 255;
	
	public static String desc(int code) {
		String desc = null;
		switch (code) {
		case ERROR_CODE_NO_ERRORS:
			desc = "No errors encountered"; break;
		case ERROR_CODE_PROCESSING_ERROR:
			desc = "Processing error"; break;
		case ERROR_CODE_MISSING_TOKEN:
			desc = "Missing device token"; break;
		case ERROR_CODE_MISSING_TOPIC:
			desc = "Missing topic"; break;
		case ERROR_CODE_MISSING_PAYLOAD:
			desc = "Missing payload"; break;
		case ERROR_CODE_INVALID_TOKEN_SIZE:
			desc = "Invalid token size"; break;
		case ERROR_CODE_INVALID_TOPIC_SIZE:
			desc = "Invalid topic size"; break;
		case ERROR_CODE_INVALID_PAYLOAD_SIZE:
			desc = "Invalid payload size"; break;
		case ERROR_CODE_INVALID_TOKEN:
			desc = "Invalid token"; break;
		case ERROR_CODE_SHUTDOWN:
			desc = "Shutdown"; break;
		default:
			desc = "Unkown"; break;
		}
		return desc;
	}
}
