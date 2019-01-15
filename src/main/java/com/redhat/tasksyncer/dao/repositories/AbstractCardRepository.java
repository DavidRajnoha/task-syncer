package com.redhat.tasksyncer.dao.repositories;

import com.redhat.tasksyncer.dao.entities.AbstractCard;
import org.springframework.data.repository.CrudRepository;

/**
 * @author Filip Cap
 */
public interface AbstractCardRepository extends CrudRepository<AbstractCard, Long> {
}
