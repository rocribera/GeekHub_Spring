package org.udg.pds.springtodo.controller;

import com.fasterxml.jackson.databind.ser.Serializers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.udg.pds.springtodo.entity.Game;
import org.udg.pds.springtodo.entity.Post;
import org.udg.pds.springtodo.service.GameService;
import org.udg.pds.springtodo.service.PostService;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collection;

@RequestMapping(path="/games")
@RestController
public class GameController extends BaseController{

    @Autowired
    GameService gameService;

    @Autowired
    PostService postService;

    @GetMapping(path="/{id}")
    public Game getGame(HttpSession session,
                        @PathVariable("id") Long id) {

        getLoggedUser(session);
        return gameService.getGame(id);
    }

    @GetMapping
    public Collection<Game> listAllGames(HttpSession session) {

        getLoggedUser(session);
        return gameService.getGames();
    }

    @GetMapping(path="/{id}/posts")
    public Collection<Post> listAllPosts(HttpSession session,
                                         @PathVariable("id") Long id) {

        getLoggedUser(session);
        return gameService.getListPosts(id);
    }

    @PostMapping(path="/{id}/posts", consumes = "application/json")
    public String uploadPost(HttpSession session, @PathVariable("id") Long id, @Valid @RequestBody UploadPost up){

        Long userId = getLoggedUser(session);

        postService.createPost(up.title,true,up.description,userId,id);
        return BaseController.OK_MESSAGE;
    }

    @DeleteMapping(path="/{id}")
    public String deleteGame(HttpSession session,
                             @PathVariable("id") Long gameId) {

        getLoggedUser(session);

        gameService.crud().deleteById(gameId);
        return BaseController.OK_MESSAGE;
    }

    static class UploadPost {
        @NotNull
        public String title;
        @NotNull
        public String description;
    }
}
