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

        Long loggedUserId = getLoggedUser(session);
        userService.deleteToken(loggedUserId);
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

    @DeleteMapping(path="/me")
    public String deleteMyUser(HttpSession session){
        Long loggedUserId = getLoggedUser(session);

        session.removeAttribute("simpleapp_auth_id");
        userService.deleteUser(loggedUserId);
        userService.crud().deleteById(loggedUserId);

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

    @PostMapping(path="/me/follows/{id}")
    public String followAPost(HttpSession session, @PathVariable("id") Long postId){
        Long loggedUserId =getLoggedUser(session);
        userService.followAPost(loggedUserId,postId);
        return BaseController.OK_MESSAGE;
    }

    @DeleteMapping(path="/me/follows/{id}")
    public String unfollowAPost(HttpSession session, @PathVariable("id") Long postId){
        Long loggedUserId =getLoggedUser(session);
        userService.unfollowAPost(loggedUserId,postId);
        return BaseController.OK_MESSAGE;
    }

    @PostMapping(path="/me/token")
    public String addToken(HttpSession session, @Valid  @RequestBody String token){
        Long loggedUserId =getLoggedUser(session);
        token = token.replace("\"", "");
        userService.addToken(loggedUserId,token);
        return BaseController.OK_MESSAGE;
    }
  
    @GetMapping(path="/me/postsFollowing")
    public Collection<Post> getUserPostsFollowing(HttpSession session){
        Long loggedUserId =getLoggedUser(session);
        return userService.getPostsFollowing(loggedUserId);
    }

    @GetMapping(path="/{id}")
    @JsonView(Views.Public.class)
    public User getUserWithID(HttpSession session, @PathVariable("id") Long userId){
        getLoggedUser(session);
        return userService.getUser(userId);
    }

    @GetMapping(path="/{id}/posts")
    public Collection<Post> getUserWithIDPosts(HttpSession session, @PathVariable("id") Long userId) {
        getLoggedUser(session);
        return userService.getPosts(userId);
    }

    @PostMapping(path="/{id}/valoration")
    public String valorateUser(HttpSession session, @PathVariable("id") Long userId, @Valid  @RequestBody String valoration){
        Long loggedUserId = getLoggedUser(session);
        valoration = valoration.replace("\"", "");
        double valor = Double.parseDouble(valoration);
        userService.updateValoration(loggedUserId,userId,valor);
        return BaseController.OK_MESSAGE;
    }

    @PostMapping(path="/me")
    public String updateProfileInfo(HttpSession session, @Valid  @RequestBody UpdateProfile updatedProfile){
        Long loggedUserId = getLoggedUser(session);
        userService.updateProfile(loggedUserId,updatedProfile.name,updatedProfile.description,updatedProfile.image);
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

    static class UpdateProfile {
        @NotNull
        public String name;

        public String description;

        public String image;
    }

}
