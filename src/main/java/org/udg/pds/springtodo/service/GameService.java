package org.udg.pds.springtodo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.udg.pds.springtodo.controller.exceptions.ServiceException;
import org.udg.pds.springtodo.entity.User;
import org.udg.pds.springtodo.repository.GameRepository;
import org.udg.pds.springtodo.entity.Game;

import java.util.Collection;
import java.util.Optional;

@Service
public class GameService {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    UserService userService;

    public GameRepository crud() {
        return gameRepository;
    }

    public Game getGame(Long id) {
        Optional<Game> g = gameRepository.findById(id);
        if (!g.isPresent()) throw new ServiceException("Game does not exists");
        return g.get();
    }

    public Collection<Game> getGames(Long id){
        Optional<User> u = userService.crud().findById(id);
        if (!u.isPresent()) throw new ServiceException("User does not exists");
        return u.get().getGames();
    }
}
