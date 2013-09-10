Introduction
==================

I'm developing an application and need to send notifications to the iOS devices.
In the past year, I had used [Java-APNS](https://github.com/notnoop/java-apns) which I think is much better than other libs,
such as JavaPNS and so on. But there are still some problem. For example, sometime an exception was thrown: java.lang.StackOverflowError.
And the device couldn't receive notifications after a period time. Then I used JSTACK and JMAP to find what happened, that's DEADLOCK, I found, 
which caused Java-APNS didn't work any more. I had to restart the service to recover it. That's terrible.
So I decide to develop a new Java client for APNS. Then dbay-apns4j comes.

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

Contact
==================
You can send an email to me: lzhc2004@163.com