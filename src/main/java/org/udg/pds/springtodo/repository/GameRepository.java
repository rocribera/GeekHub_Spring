package org.udg.pds.springtodo.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.udg.pds.springtodo.entity.Game;

import java.util.List;

@Component
public interface GameRepository extends CrudRepository<Game, Long> {
    @Query("SELECT g FROM games g")
    List<Game> getAllGames();

    @Query("SELECT g FROM games g WHERE g.name=:name")
    List<Game> findByName(@Param("name") String name);
}
