package org.udg.pds.springtodo.entity;

import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

@Entity
public class Category implements Serializable {

    public Category() {}

    public Category(String name, String description){
        this.name = name;
        this.description = description;
        this.games = new ArrayList<>();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Private.class)
    private Long id;

    @NotNull
    @JsonView(Views.Public.class)
    private String name;

    @NotNull
    @JsonView(Views.Public.class)
    private String description;

    @ManyToMany(cascade = CascadeType.ALL)
    private Collection<Game> games;

    public Long getId() {return id; }

    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }

    public String getDescription() { return description; }

    public Collection<Game> getGames() { return games; }

    public void addGame(Game game) { games.add(game); }
}

