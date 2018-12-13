package com.redhat.tasksyncer.exceptions;

/**
 * @author Filip Cap
 */
public class TaskSyncerException extends Exception {
    public TaskSyncerException() {
    }

    public TaskSyncerException(String message) {
        super(message);
    }

    public TaskSyncerException(String message, Throwable cause) {
        super(message, cause);
    }

    public TaskSyncerException(Throwable cause) {
        super(cause);
    }
}
