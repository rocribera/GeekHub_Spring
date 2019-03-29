package org.udg.pds.springtodo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
public class Post implements Serializable {

    public Post() {}

    public Post(String title,Boolean active, String description){
        this.title = title;
        this.description = description;
        this.active = active;
    }

    public Post(String title,Boolean active, String description, Game game, User user){
        this.title = title;
        this.description = description;
        this.active = active;
        this.game = game;
        this.user = user;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Private.class)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_user")
    private User user;

    @Column(name = "fk_user", insertable = false, updatable = false)
    private long userId;

    @NotNull
    @JsonView(Views.Public.class)
    private String title;

    @JsonView(Views.Private.class)
    private boolean active;

    @NotNull
    @JsonView(Views.Public.class)
    private String description;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    private Game game;

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }

    public String getTitle() { return title; }

    public boolean getActive() { return active; }

    public String getDescription() { return description; }

    public void setActive(Boolean active) { this.active = active; }

    public Game getGame() { return game; }

    public User getUser() { return user; }

    public void setUser(User user) {
        this.user = user;
    }

    public void setGame(Game game) {
        this.game = game;
    }
}
