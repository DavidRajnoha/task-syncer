package com.redhat.tasksyncer.exceptions;

public class SynchronizationFailedException extends Exception {

    public SynchronizationFailedException(String err){
        super(err);
    }
}
