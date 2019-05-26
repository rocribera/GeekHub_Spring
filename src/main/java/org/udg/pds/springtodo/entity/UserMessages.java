package org.udg.pds.springtodo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
public class UserMessages implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Public.class)
    protected Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnore
    private User user1;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnore
    private User user2;

    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @JsonView(Views.Public.class)
    private List<Message> messages = new ArrayList<>();


    private UserMessages() {}

    public UserMessages(User user1, User user2){
        this.user1 = user1;
        this.user2 = user2;
    }

    public void setId(Long id) { this.id = id; }

    public Long getId() { return id; }

    public List<Message> getMessages() {return messages; }

    public void addMessage(Message m) { messages.add(m); }

    public User getUser1() { return user1; }

    public User getUser2() { return user2; }

    @Override
    public boolean equals(Object obj) {
        return (((UserMessages) obj).user1 == user1 && ((UserMessages) obj).user2 == user2) || (((UserMessages) obj).user1 == user2 && ((UserMessages) obj).user2 == user1);
    }

    public boolean compare(User user1, User user2){
        return (this.user1 == user1 && this.user2 == user2) || (this.user1 == user2 && this.user2 == user1);
    }
}
