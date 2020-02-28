package com.redhat.tasksyncer.dao.repositories;

import com.redhat.tasksyncer.dao.entities.trello.AbstractCard;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Filip Cap
 */
public interface AbstractCardRepository extends JpaRepository<AbstractCard, Long> {
}
