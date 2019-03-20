package org.udg.pds.springtodo.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.udg.pds.springtodo.entity.Game;

import java.util.List;

@Component
public interface GameRepository extends CrudRepository<Game, Long> {
    @Query("SELECT g FROM games g")
    List<Game> getAllGames();
}
