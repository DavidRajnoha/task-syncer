package com.redhat.tasksyncer.exceptions;

public class CannotConnectToRepositoryException extends Exception {
    public CannotConnectToRepositoryException(String message) {
        super(message);
    }
}
