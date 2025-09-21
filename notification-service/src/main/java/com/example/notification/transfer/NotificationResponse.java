package com.example.notification.transfer;

import com.example.common.enumeration.OperationStatus;
import com.example.common.transfer.Response;


public class NotificationResponse extends Response {

    public NotificationResponse() {
        super();
        this.setMessage("Notification processed successfully");
        this.setStatus(OperationStatus.SUCCESS);
    }


}
