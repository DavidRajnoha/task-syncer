package com.redhat.tasksyncer.dao.repositories;

import com.redhat.tasksyncer.dao.entities.AbstractCard;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Filip Cap
 */
public interface AbstractCardRepository extends JpaRepository<AbstractCard, Long> {
}
