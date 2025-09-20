package com.example.notification.transfer;

import com.example.core.enumeration.OperationStatus;
import com.example.core.transfer.Response;


public class NotificationResponse extends Response {

    public NotificationResponse() {
        super();
        this.setMessage("Notification processed successfully");
        this.setStatus(OperationStatus.SUCCESS);
    }


}
