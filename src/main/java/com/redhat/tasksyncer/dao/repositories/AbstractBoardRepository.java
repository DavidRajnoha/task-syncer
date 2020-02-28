package com.redhat.tasksyncer.dao.repositories;

import com.redhat.tasksyncer.dao.entities.trello.AbstractBoard;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Filip Cap
 */
public interface AbstractBoardRepository extends JpaRepository<AbstractBoard, Long> {
}
