package com.redhat.tasksyncer.exceptions;

/**
 * @author Filip Cap
 */
public class ProjectNotFoundException extends TaskSyncerException {
    public ProjectNotFoundException() {
    }

    public ProjectNotFoundException(String message) {
        super(message);
    }

    public ProjectNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProjectNotFoundException(Throwable cause) {
        super(cause);
    }
}
