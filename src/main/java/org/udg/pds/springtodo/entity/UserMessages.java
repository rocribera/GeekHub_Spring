package org.udg.pds.springtodo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.udg.pds.springtodo.serializer.JsonUserMessagesSerializer;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@JsonSerialize(using = JsonUserMessagesSerializer.class)
public class UserMessages implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    protected Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonView(Views.Public.class)
    private User user1;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonView(Views.Public.class)
    private User user2;

    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @JsonIgnore
    private List<Message> messages = new ArrayList<>();

    @JsonView(Views.Public.class)
    private boolean active;

    @JsonView(Views.Public.class)
    private long block;

    private UserMessages() {}

    public UserMessages(User user1, User user2){
        this.user1 = user1;
        this.user2 = user2;
        this.active = true;
        this.block = 0;
    }

    public void setId(Long id) { this.id = id; }

    public Long getId() { return id; }

    public List<Message> getMessages() {return messages; }

    public void addMessage(Message m) { messages.add(m); }

    public User getUser1() { return user1; }

    public User getUser2() { return user2; }

    public boolean isActive() { return active; }

    public void setActive(boolean active) { this.active = active; }

    @Override
    public boolean equals(Object obj) {
        return (((UserMessages) obj).user1 == user1 && ((UserMessages) obj).user2 == user2) || (((UserMessages) obj).user1 == user2 && ((UserMessages) obj).user2 == user1);
    }

    public boolean compare(User user1, User user2){
        return (this.user1 == user1 && this.user2 == user2) || (this.user1 == user2 && this.user2 == user1);
    }

    public long getBlock() { return block; }

    public void setBlock(long block) { this.block = block; }
}
