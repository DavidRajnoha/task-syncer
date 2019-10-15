package com.redhat.tasksyncer.dao.repositories;

import com.redhat.tasksyncer.dao.entities.AbstractBoard;
import com.redhat.tasksyncer.dao.entities.AbstractColumn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * @author Filip Cap
 */
public interface AbstractColumnRepository extends JpaRepository<AbstractColumn, Long> {
    List<AbstractColumn> findByBoard_Id(Long id);
}
