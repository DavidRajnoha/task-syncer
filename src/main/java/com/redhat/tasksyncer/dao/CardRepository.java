package com.redhat.tasksyncer.dao;

import com.redhat.tasksyncer.dao.entities.Card;
import org.springframework.data.repository.CrudRepository;

/**
 * @author Filip Cap
 */
public interface CardRepository extends CrudRepository<Card, Long> {
}
