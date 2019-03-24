package org.udg.pds.springtodo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.udg.pds.springtodo.serializer.JsonDateDeserializer;
import org.udg.pds.springtodo.serializer.JsonDateSerializer;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

@Entity(name = "games")
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"name"}))
public class Game implements Serializable {

    public Game() {}

    public Game(String name, String image, String description){
        this.name = name;
        this.image = image;
        this.description = description;
        this.posts = new ArrayList<>();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Private.class)
    private Long id;

    @NotNull
    @JsonView(Views.Public.class)
    private String name;

    @ManyToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @JsonView(Views.Public.class)
    private Collection<Category> categories = new ArrayList<>();

    @NotNull
    @JsonView(Views.Public.class)
    private String image;

    @JsonSerialize(using = JsonDateSerializer.class)
    @JsonDeserialize(as= JsonDateDeserializer.class)
    @JsonView(Views.Public.class)
    private Date dateLastPost;

    @NotNull
    @JsonView(Views.Public.class)
    private String description;

    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER, mappedBy = "game")
    @Fetch(value = FetchMode.SUBSELECT)
    @JsonIgnore
    private Collection<Post> posts;

    @ManyToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER, mappedBy = "games")
    @Fetch(value = FetchMode.SUBSELECT)
    @JsonIgnore
    private Collection<User> users = new ArrayList<>();

    @JsonView(Views.Private.class)
    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }

    public String getDescription() { return description; }

    public Collection<Post> getPosts() {
        posts.size();
        return posts;
    }

    public Collection<User> getUsers() {
        users.size();
        return users;
    }

    public Date getDateLastPost() { return dateLastPost; }

    public void setDataLastPost(Date dateLastPost) {
        this.dateLastPost = dateLastPost;
    }

    public String getImage() { return image; }

    public Collection<Category> getCategories() { return categories; }

    public void addPost(Post post) { posts.add(post); }

    public void addUser(User user) { users.add(user); }

    public void addCategory(Category category) { categories.add(category); }

}
