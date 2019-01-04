package com.redhat.tasksyncer;

import com.redhat.tasksyncer.dao.entities.Endpoint;
import com.redhat.tasksyncer.dao.entities.Endpoint.EndpointType;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * @author Filip Cap
 */
public interface EndpointRepository extends CrudRepository<Endpoint, Long> {
    public Optional<Endpoint> findByEndpointTypeAndField(EndpointType type, String field);
}
