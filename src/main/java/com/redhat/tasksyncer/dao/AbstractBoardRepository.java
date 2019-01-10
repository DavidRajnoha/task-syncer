package com.redhat.tasksyncer.dao;

import com.redhat.tasksyncer.dao.entities.AbstractBoard;
import org.springframework.data.repository.CrudRepository;

/**
 * @author Filip Cap
 */
public interface AbstractBoardRepository extends CrudRepository<AbstractBoard, Long> {
}
