package com.redhat.tasksyncer.converters;

import com.redhat.tasksyncer.dao.entities.Issue;

/**
 * @author Filip Cap
 */
public interface ObjectToIssueConverter<T> {
    Issue convert(T object);
}
