package com.redhat.tasksyncer.dao.repositories;

import com.redhat.tasksyncer.dao.entities.trello.AbstractColumn;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author Filip Cap
 */
public interface AbstractColumnRepository extends JpaRepository<AbstractColumn, Long> {
    List<AbstractColumn> findByBoard_Id(Long id);
}
