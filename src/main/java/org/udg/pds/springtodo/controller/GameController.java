package org.udg.pds.springtodo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.udg.pds.springtodo.entity.Game;
import org.udg.pds.springtodo.service.GameService;

import javax.servlet.http.HttpSession;
import java.util.Collection;

@RequestMapping(path="/games")
@RestController
public class GameController extends BaseController{

    @Autowired
    GameService gameService;

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

    @DeleteMapping(path="/{id}")
    public String deleteGame(HttpSession session,
                             @PathVariable("id") Long gameId) {

        getLoggedUser(session);

        gameService.crud().deleteById(gameId);
        return BaseController.OK_MESSAGE;
    }
}
