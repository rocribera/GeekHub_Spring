package org.udg.pds.springtodo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.udg.pds.springtodo.repository.GameRepository;
import org.udg.pds.springtodo.entity.Game;

@Service
public class GameService {

    @Autowired
    private GameRepository gameRepository;

    public GameRepository crud() {
        return gameRepository;
    }
}
