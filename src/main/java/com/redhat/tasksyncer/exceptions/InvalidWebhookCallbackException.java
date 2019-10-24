package com.redhat.tasksyncer.exceptions;

public class InvalidWebhookCallbackException extends Exception {
    public InvalidWebhookCallbackException(String err){
        super(err);
    }
}
