package org.udg.pds.springtodo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.udg.pds.springtodo.serializer.JsonPostSerializer;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@JsonSerialize(using = JsonPostSerializer.class)
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
        followers = new ArrayList<>();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Private.class)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

    @NotNull
    @JsonView(Views.Public.class)
    private String title;

    @JsonView(Views.Private.class)
    private boolean active;

    @NotNull
    @JsonView(Views.Public.class)
    private String description;

    @ManyToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @JsonIgnore
    private Collection<User> followers;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    private Game game;

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

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

    public Collection<User> getFollowers() {
        followers.size();
        return followers;
    }

    public void addUserFollowing(User user){ followers.add(user); }

    public void removeUserFollowing(User user) { followers.remove(user); }

    public void setDescription(String desc) { this.description = desc; }
}
