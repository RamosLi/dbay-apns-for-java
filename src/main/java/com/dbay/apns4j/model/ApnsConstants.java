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

/**
 * @author RamosLi
 *
 */
public class ApnsConstants {
	// host and port for development environment
	public static final String HOST_DEVELOPMENT_ENV = "gateway.sandbox.push.apple.com";
	public static final int PORT_DEVELOPMENT_ENV = 2195;
	
	// host and port for production environment
	public static final String HOST_PRODUCTION_ENV = "gateway.push.apple.com";
	public static final int PORT_PRODUCTION_ENV = 2195;
	
	public static final String FEEDBACK_HOST_DEVELOPMENT_ENV = "feedback.sandbox.push.apple.com";
	public static final int FEEDBACK_PORT_DEVELOPMENT_ENV = 2196;
	
	public static final String FEEDBACK_HOST_PRODUCTION_ENV = "feedback.push.apple.com";
	public static final int FEEDBACK_PORT_PRODUCTION_ENV = 2196;
	
	public static final String KEYSTORE_TYPE = "PKCS12";
	public static final String ALGORITHM = "sunx509";
	public static final String PROTOCOL = "TLS";
	
	public static final int ERROR_RESPONSE_BYTES_LENGTH = 6;
	
	public static final int PAY_LOAD_MAX_LENGTH = 256;
	
	public static final String CHARSET_ENCODING = "UTF-8";
}
