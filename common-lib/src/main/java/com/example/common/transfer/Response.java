package com.example.common.transfer;

import com.example.common.enumeration.OperationStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public abstract class Response {
    private OperationStatus status = OperationStatus.SUCCESS;
    private String message;

    public Response(OperationStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public void fail(String message) {
        this.status = OperationStatus.FAILURE;
        this.message = message;
    }

    public void fail() {
        this.status = OperationStatus.FAILURE;
        this.message = "Operation Failed";
    }

    public boolean isSuccess() {
        return this.getStatus() == OperationStatus.SUCCESS;
    }
}
