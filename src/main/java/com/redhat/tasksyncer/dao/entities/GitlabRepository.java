package com.redhat.tasksyncer.dao.entities;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

import javax.persistence.Entity;

/**
 * @author Filip Cap
 */

@Entity
@PropertySource("classpath:other.properties")
public class GitlabRepository extends AbstractRepository {


    @Value("${gitlabURL}")
    private String gitlabURL;

    @Value("${gitlabAuthKey}")
    private String gitlabAuthKey;

    public GitlabRepository() {
        super();
    }
}
