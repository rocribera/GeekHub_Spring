package org.udg.pds.springtodo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

@Entity(name = "users")
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"email", "username"}))
public class User implements Serializable {
    /**
     * Default value included to remove warning. Remove or modify at will. *
     */
    private static final long serialVersionUID = 1L;

    public User() {
    }

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.ownPosts = new ArrayList<>();
        this.valoration = null;
        this.image = null;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Private.class)
    protected Long id;

    @NotNull
    @JsonView(Views.Public.class)
    private String username;

    @NotNull
    @JsonView(Views.Private.class)
    private String email;

    @NotNull
    @JsonIgnore
    private String password;

    @JsonView(Views.Public.class)
    private String valoration;

    @JsonView(Views.Public.class)
    private String description;

    @JsonView(Views.Public.class)
    private String image;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user",fetch = FetchType.EAGER)
    @JsonIgnore
    @Fetch(value = FetchMode.SUBSELECT)
    private Collection<Post> ownPosts;

    @ManyToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @JsonView(Views.Private.class)
    private Collection<Game> games = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public Collection<Post> getPosts() {
        // Since tasks is collection controlled by JPA, it has LAZY loading by default. That means
        // that you have to query the object (calling size(), for example) to get the list initialized
        // More: http://www.javabeat.net/jpa-lazy-eager-loading/
        ownPosts.size();
        return ownPosts;
    }

    public Collection<Game> getGames() {
        games.size();
        return games;
    }

    public void addGame(Game game) { games.add(game); }

    public void addPost(Post post) { ownPosts.add(post); }

    public String getValoration() { return valoration; }

    public void setValoration(String val) { this.valoration = val; }

    public String getDescription() { return description; }

    public void setDescription(String desc) { this.description = desc; }

    public String getImage() { return this.image; }

    public void setImage(String image) { this.image = image; }

}
