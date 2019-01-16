package com.redhat.tasksyncer.dao.repositories;

import com.redhat.tasksyncer.dao.entities.AbstractBoard;
import com.redhat.tasksyncer.dao.entities.AbstractColumn;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * @author Filip Cap
 */
public interface AbstractColumnRepository extends CrudRepository<AbstractColumn, Long> {
    List<AbstractColumn> findAllByBoardId(Long boardId);
}
