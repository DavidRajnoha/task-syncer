package com.redhat.tasksyncer.dao.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Filip Cap
 */
@Entity
@Inheritance
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public abstract class AbstractColumn {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String remoteColumnId;

    @OneToMany(targetEntity = AbstractCard.class, fetch = FetchType.LAZY, mappedBy = "column")
    @JsonManagedReference
    private List<AbstractCard> cards;

    @ManyToOne(targetEntity = AbstractBoard.class, fetch = FetchType.LAZY, optional = false)
    @JsonBackReference
    private AbstractBoard board;

    public static final String TODO_DEFAULT = "TODO";
    public static final String DONE_DEFAULT = "DONE";


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

    public void addCard(AbstractCard card){
        if (this.cards == null) {
            this.cards = new ArrayList<>();
        }

        if (this.cards.contains(card)) return;

        this.cards.add(card);

        card.setColumn(this);
    }

    public void removeCard(AbstractCard card){
        if (this.cards == null || !this.cards.contains(card)) return;

        this.cards.remove(card);

        card.setColumn(null);
    }

    public AbstractBoard getBoard() {
        return board;
    }

    public void setBoard(AbstractBoard board) {
        if (Objects.equals(this.board, board)) return;

        if (this.board != null) this.board.removeColumn(this);

        this.board = board;

        if (this.board != null) this.board.addColumn(this);
    }
}
