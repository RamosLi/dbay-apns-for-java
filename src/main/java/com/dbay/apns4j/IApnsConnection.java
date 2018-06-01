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
package com.dbay.apns4j;

import java.io.Closeable;

import com.dbay.apns4j.model.Payload;
import com.dbay.apns4j.model.PushNotification;

public interface IApnsConnection extends Closeable {
	
	public Boolean sendNotification(String token, Payload payload);

	public Boolean sendNotification(PushNotification notification);
}
