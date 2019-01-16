package com.redhat.tasksyncer.dao.entities;

import javax.persistence.*;
import java.util.List;

/**
 * @author Filip Cap
 */
@Entity
@Inheritance
public abstract class AbstractColumn {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String remoteColumnId;

    @OneToMany(targetEntity = AbstractCard.class, fetch = FetchType.LAZY, mappedBy = "column")
    private List<AbstractCard> cards;

    @ManyToOne(targetEntity = AbstractBoard.class, fetch = FetchType.LAZY, optional = false)
    private AbstractBoard board;


    public AbstractColumn() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemoteColumnId() {
        return remoteColumnId;
    }

    public void setRemoteColumnId(String remoteColumnId) {
        this.remoteColumnId = remoteColumnId;
    }

    public List<AbstractCard> getCards() {
        return cards;
    }

    public void setCards(List<AbstractCard> cards) {
        this.cards = cards;
    }

    public AbstractBoard getBoard() {
        return board;
    }

    public void setBoard(AbstractBoard board) {
        this.board = board;
    }
}
