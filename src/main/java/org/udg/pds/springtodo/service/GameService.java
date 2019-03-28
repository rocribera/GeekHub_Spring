package org.udg.pds.springtodo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.udg.pds.springtodo.controller.exceptions.ServiceException;
import org.udg.pds.springtodo.entity.Category;
import org.udg.pds.springtodo.repository.GameRepository;
import org.udg.pds.springtodo.entity.Game;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class GameService {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private CategoryService categoryService;

    public GameRepository crud() {
        return gameRepository;
    }

    public Game getGame(Long id) {
        Optional<Game> g = gameRepository.findById(id);
        if (!g.isPresent()) throw new ServiceException("Game does not exists");
        return g.get();
    }

    public Collection<Game> getGames(){
        Collection<Game> g = gameRepository.getAllGames();
        if(g.isEmpty()) throw new ServiceException("There is no games in the DB");
        return g;
    }

    public Game createGame(String name, String image, String description){
        List<Game> uName = gameRepository.findByName(name);
        if(uName.size() > 0)
            throw new ServiceException("Name already exist");

        Game ng = new Game(name, image, description);
        gameRepository.save(ng);
        return ng;
    }

    @Transactional
    public void addCategory(Long gameId, List<Long> categories){
        Game g = this.getGame(gameId);

        if (g.getId() != gameId)
            throw new ServiceException(("This game is not in the DB"));

        try{
            for(Long catId : categories) {
                Optional<Category> ocat = categoryService.crud().findById(catId);
                if (ocat.isPresent()) {
                    g.addCategory(ocat.get());
                    ocat.get().addGame(g);
                }
                else
                    throw new ServiceException("Category does not exists");
            }
        } catch (Exception ex) {
          // Very important: if you want that an exception reaches the EJB caller, you have to throw an ServiceException
          // We catch the normal exception and then transform it in a ServiceException
          throw new ServiceException(ex.getMessage());
        }
    }

}
