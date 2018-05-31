package com.dbay.apns4j;

import com.dbay.apns4j.model.Payload;

public interface IApnsCallback {
    public void success(Payload payload);
}
