package org.udg.pds.springtodo.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.udg.pds.springtodo.controller.exceptions.ControllerException;
import org.udg.pds.springtodo.entity.Post;
import org.udg.pds.springtodo.entity.User;
import org.udg.pds.springtodo.entity.Views;
import org.udg.pds.springtodo.service.PostService;
import org.udg.pds.springtodo.service.UserService;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collection;

// This class is used to process all the authentication related URLs
@RequestMapping(path="/users")
@RestController
public class UserController extends BaseController {

    // This is the EJB used to access user data
    @Autowired
    UserService userService;

    @Autowired
    PostService postService;

    @PostMapping(path="/login")
    @JsonView(Views.Private.class)
    public User login(HttpSession session, @Valid @RequestBody LoginUser user) {

        checkNotLoggedIn(session);

        User u = userService.matchPassword(user.username, user.password);
        session.setAttribute("simpleapp_auth_id", u.getId());
        return u;
    }

    @PostMapping(path="/logout")
    @JsonView(Views.Private.class)
    public String logout(HttpSession session) {

        getLoggedUser(session);

        session.removeAttribute("simpleapp_auth_id");
        return BaseController.OK_MESSAGE;
    }


    @DeleteMapping(path="/{id}")
    public String deleteUser(HttpSession session, @PathVariable("id") Long userId) {

        Long loggedUserId = getLoggedUser(session);

        if (!loggedUserId.equals(userId))
            throw new ControllerException("You cannot delete other users!");

        userService.crud().deleteById(userId);
        session.removeAttribute("simpleapp_auth_id");

        return BaseController.OK_MESSAGE;
    }


    @PostMapping(path="/register", consumes = "application/json")
    public User register(HttpSession session, @Valid  @RequestBody RegisterUser ru) {

        checkNotLoggedIn(session);
        userService.register(ru.username, ru.email, ru.password);

        User u = userService.matchPassword(ru.username, ru.password);
        session.setAttribute("simpleapp_auth_id", u.getId());
        return u;
    }

    @GetMapping(path="/me")
    @JsonView(Views.Complete.class)
    public User getUserProfile(HttpSession session) {

        Long loggedUserId = getLoggedUser(session);

        return userService.getUserProfile(loggedUserId);
    }

    @GetMapping(path="/check")
    public String checkLoggedIn(HttpSession session) {

        getLoggedUser(session);

        return BaseController.OK_MESSAGE;
    }

    @PostMapping(path="/me/games/{id}")
    public String bookmarkGame(HttpSession session, @PathVariable("id") Long gameId){

        Long loggedUserId = getLoggedUser(session);
        userService.addGame(loggedUserId,gameId);

        return BaseController.OK_MESSAGE;
    }

    @DeleteMapping(path="/me/games/{id}")
    public String unsubscribeGame(HttpSession session, @PathVariable("id") Long gameId){
        Long loggedUserId =getLoggedUser(session);
        userService.deleteGame(loggedUserId,gameId);
        return BaseController.OK_MESSAGE;
    }

    @GetMapping(path="/me/posts")
    public Collection<Post> getUserPosts(HttpSession session){
        Long loggedUserId =getLoggedUser(session);
        return userService.getPosts(loggedUserId);
    }

    @PostMapping(path="/me/posts/{id}")
    public String changeState(HttpSession session, @PathVariable("id") Long postId){
        Long loggedUserId =getLoggedUser(session);
        postService.changeState(postId,loggedUserId);
        return BaseController.OK_MESSAGE;
    }


    static class LoginUser {
        @NotNull
        public String username;
        @NotNull
        public String password;
    }

    static class RegisterUser {
        @NotNull
        public String username;
        @NotNull
        public String email;
        @NotNull
        public String password;
    }

    static class ID {
        public Long id;

        public ID(Long id) {
            this.id = id;
        }
    }

}
