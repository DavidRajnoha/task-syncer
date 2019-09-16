package com.redhat.tasksyncer.exceptions;

public class RepositoryTypeNotSupportedException extends Exception {
    public RepositoryTypeNotSupportedException(String err){
        super(err);
    }
}
