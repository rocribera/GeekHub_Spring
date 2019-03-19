package org.udg.pds.springtodo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

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
    this.posts = new ArrayList<>();
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

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
  @JsonView(Views.Complete.class)
  private Collection<Post> posts;

  @ManyToMany(cascade = CascadeType.ALL)
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
    posts.size();
    return posts;
  }

  public void addTask(Post post) {
    posts.add(post);
  }

  public Collection<Game> getGames() { return games; }

}
