Introduction
==================

I'm developing an application and need to send notifications to the iOS devices.
In the past year, I had used [Java-APNS](https://github.com/notnoop/java-apns) which I think is much better than other libs,
such as JavaPNS and so on. But there are still some problem. For example, sometime an exception was thrown: java.lang.StackOverflowError.
And the device couldn't receive notifications after a period time. Then I used JSTACK and JMAP to find what happened, that's DEADLOCK, I found, 
which caused Java-APNS didn't work any more. I had to restart the service to recover it. That's terrible.
So I decide to develop a new Java client for APNS. Then dbay-apns4j comes. It's best, I think.

Features
==================
* High performance and easy to use
* Gets started quickly with demos
* Supports connection pooling
* 中英双语注释，英语小白阅读起来也没问题
* Supports resend notifications after error
* Creates new socket automatically when idle
* Supports the Feedback Service
* Supports the sandbox and production services
* Detailed and useful log

Demos
==================
You can find demo in the package.
In this file: com.dbay.apns4j.demo.Apns4jDemo.java

Sample code
==================
1. Create an ApnsService

        private static IApnsService apnsService;
        if (apnsService == null) {
            ApnsConfig config = new ApnsConfig();
	    InputStream is = Apns4jDemo.class.getClassLoader().getResourceAsStream("Certificate.p12");
	    config.setKeyStore(is);
	    config.setDevEnv(false);
	    config.setPassword("123123");
	    config.setPoolSize(5);
	    apnsService = ApnsServiceImpl.createInstance(config);
        }

2. Send notification

        String token = "94c4764e4545f41a7b2052692c8a9b41f9c5c925876e11fec5721d9074ee5e5a";
        Payload payload = new Payload();
        payload.setAlert("Hello, how are you?");
        //payload.setAlertBody("alert body");
        //payload.setAlertLocKey("use emotion ok");
        //payload.setAlertLocArgs(new String[]{"3"});
        //payload.setAlertActionLocKey("lbg_loading_text_0");
        payload.setBadge(1);
        payload.setSound("msg.mp3");
        //payload.addParam("uid", 123456);
        //payload.addParam("type", 12);
        //payload.setContentAvailable(1);

        service.sendNotification(token, payload);

Contact
==================
If you are using dbay-apns4j, let me know and keep in touch, thx.
You can send an email to me: lzhc2004@163.com

Tips
==================
2014-09-22：ios8更新后，如果没有设置声音(Payload.setSound())，那么手机收到推送很可能不响。
此时需要更新dbay-apns4j包，或者自己设置下声音。eg: Payload.setSound("xx")。注意不能设为空串，否则不响，设为任意串均可。
