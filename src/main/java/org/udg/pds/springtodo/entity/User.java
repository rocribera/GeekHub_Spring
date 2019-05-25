package org.udg.pds.springtodo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.*;

@Entity(name = "users")
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"email", "name"}))
public class User implements Serializable {
    /**
     * Default value included to remove warning. Remove or modify at will. *
     */
    private static final long serialVersionUID = 1L;

    public User() {
    }

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.ownPosts = new ArrayList<>();
        this.valoration=2.5;
        this.image = null;
        this.token = null;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Public.class)
    protected Long id;

    @NotNull
    @JsonView(Views.Public.class)
    private String name;

    @JsonView(Views.Public.class)
    private String token;

    @NotNull
    @JsonView(Views.Private.class)
    private String email;

    @NotNull
    @JsonIgnore
    private String password;

    @JsonView(Views.Public.class)
    private double valoration;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL,mappedBy = "userValorating",fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    private Collection<UserValoration> userValorating = new ArrayList<>();

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL,mappedBy = "userValorated",fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    private Collection<UserValoration> userValorated = new ArrayList<>();

    @JsonView(Views.Public.class)
    private String description;

    @JsonView(Views.Public.class)
    private String image;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user",fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @JsonIgnore
    private Collection<Post> ownPosts;

    @ManyToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @JsonView(Views.Private.class)
    private Collection<Game> games = new ArrayList<>();

    @ManyToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER, mappedBy = "followers")
    @Fetch(value = FetchMode.SUBSELECT)
    @JsonView(Views.Private.class)
    private Collection<Post> followedPosts = new ArrayList<>();

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL,mappedBy = "user1", fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    private List<UserMessages> chatsUser1 = new ArrayList<>();

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL,mappedBy = "user2", fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    private List<UserMessages> chatsUser2 = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken(){ return token; }

    public void setToken(String tok) { this.token = tok; }

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

    public double getValoration() { return valoration; }

    public void setValoration(double val) { this.valoration = val; }

    public String getDescription() { return description; }

    public void setDescription(String desc) { this.description = desc; }

    public String getImage() { return this.image; }

    public void setImage(String image) { this.image = image; }

    public Collection<Post> getFollowedPosts() {
        followedPosts.size();
        return followedPosts;
    }

    public void addPostFollowing(Post post){ followedPosts.add(post); }

    public void removePostFollowing(Post post) { followedPosts.remove(post); }

    public String getName() { return name; }

    public void removeOwnPost(Post post) { ownPosts.remove(post); }

    public Collection<UserValoration> getUserValorated() { return userValorated; }

    public Collection<UserValoration> getUserValorating() { return userValorating; }

    public void addUserValorating(UserValoration uv) {
        userValorating.add(uv);
    }

    public void addUserValorated(UserValoration uv) {
        userValorated.add(uv);
    }

    public void setName(String name) { this.name = name; }

    public void removeGame(Game game) { this.games.remove(game); }

    public void removeUserValorated(UserValoration uv) { userValorated.remove(uv); }

    public void removeUserValorating(UserValoration uv) { userValorating.remove(uv); }

    public List<UserMessages> getChatsUser1() {return chatsUser1; }

    public List<UserMessages> getChatsUser2() { return chatsUser2; }

    public void addNewChatUser1(UserMessages um) { chatsUser1.add(um); }

    public void addNewChatUser2(UserMessages um) { chatsUser2.add(um); }
}
