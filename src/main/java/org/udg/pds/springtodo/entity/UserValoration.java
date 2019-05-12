package org.udg.pds.springtodo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;

@Entity
public class UserValoration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.Private.class)
    protected Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnore
    private User userValorated;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnore
    private User userValorating;

    @Column(name = "valoration")
    private double valoration;

    private UserValoration() {}

    public UserValoration(User userValorating, User userValorated, double valoration){
        this.userValorated = userValorated;
        this.userValorating = userValorating;
        this.valoration = valoration;
    }

    public Long getId() { return id; }

    public double getValoration() { return valoration; }

    public User getUserValorated() { return userValorated; }

    public User getUserValorating() { return userValorating; }

    public void setUserValorated(User userValorated) { this.userValorated = userValorated; }

    public void setId(Long id) { this.id = id; }

    public void setUserValorating(User userValorating) { this.userValorating = userValorating; }

    public void setValoration(double valoration) { this.valoration = valoration; }
}

