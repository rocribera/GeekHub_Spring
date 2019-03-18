package org.udg.pds.springtodo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.udg.pds.springtodo.serializer.JsonDateDeserializer;
import org.udg.pds.springtodo.serializer.JsonDateSerializer;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

@Entity
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
    @JsonView(Views.Private.class)
    private String name;

    @NotNull
    @JsonView(Views.Private.class)
    private String image;

    @JsonSerialize(using = JsonDateSerializer.class)
    @JsonDeserialize(as= JsonDateDeserializer.class)
    private Date dateLastPost;

    @NotNull
    @JsonView(Views.Private.class)
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_category")
    private Category category;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "post")
    @JsonView(Views.Complete.class)
    private Collection<Post> posts;

    @ManyToMany(cascade = CascadeType.ALL)
    private Collection<User> users = new ArrayList<>();

    public Long getId() {return return id; }

    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }

    public String getDescription() { return description; }

    public Collection<Post> getPosts() { return posts; }

    public Collection<User> getUsers() { return users; }

    public Date getDateLastPost() { return dateLastPost; }

    public String getImage() { return image; }

    public Category getCategory() { return category; }

    public void addPost(Post post) { posts.add(post); }

    public void addUser(User user) { users.add(user); }

    public void setCategory(Category category) { this.category = category; }

}
