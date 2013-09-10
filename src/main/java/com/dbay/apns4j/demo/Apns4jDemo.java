package com.dbay.apns4j.demo;

import java.io.InputStream;
import java.util.List;

import com.dbay.apns4j.IApnsService;
import com.dbay.apns4j.impl.ApnsServiceImpl;
import com.dbay.apns4j.model.ApnsConfig;
import com.dbay.apns4j.model.Feedback;
import com.dbay.apns4j.model.Payload;

/**
 * @author RamosLi
 *
 */
public class Apns4jDemo {
	private static IApnsService apnsService;
	
	private static IApnsService getApnsService() {
		if (apnsService == null) {
			ApnsConfig config = new ApnsConfig();
			InputStream is = Apns4jDemo.class.getClassLoader().getResourceAsStream("Certificate.p12");
			config.setKeyStore(is);
			config.setDevEnv(false);
			config.setPassword("123123");
			config.setPoolSize(5);
			apnsService = ApnsServiceImpl.createInstance(config);
		}
		return apnsService;
	}
	
	public static void main(String[] args) throws Exception {
		IApnsService service = getApnsService();
		
		// send notification
		String token = "94c4764e4645f42a7b2052692c8b5b41f9d5c925876e11fec5721e9045ee4e5b";
		Payload payload = new Payload();
		payload.setAlert("How are you?");
		payload.setBadge(1);
		payload.setSound("msg.mp3");
		payload.addParam("uid", 123456);
		payload.addParam("type", 12);
		service.sendNotification(token, payload);
		
		// get feedback
		List<Feedback> list = service.getFeedbacks();
		if (list != null && list.size() > 0) {
			for (Feedback feedback : list) {
				System.out.println(feedback.getDate() + " " + feedback.getToken());
			}
		}
		
		// It's a good habit to shutdown what you never use
		service.shutdown();
		
		System.exit(0);
	}
}
