package org.udg.pds.springtodo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class Message implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    protected Long id;

    @JsonView(Views.Private.class)
    private String createdAt;

    @JsonView(Views.Private.class)
    private String message;

    @JsonView(Views.Private.class)
    private Long senderId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnore
    private UserMessages userMessages;

    private Message() {}

    public Message(String message, String createdAt,Long senderId,UserMessages userMessages){
        this.createdAt = createdAt;
        this.message = message;
        this.senderId = senderId;
        this.userMessages = userMessages;
    }

    public Long getSenderId() {
        return senderId;
    }

    public String getMessage() {
        return message;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
