package org.udg.pds.springtodo.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.udg.pds.springtodo.entity.Category;

import java.util.List;

@Component
public interface CategoryRepository extends CrudRepository<Category, Long> {
    @Query("SELECT c FROM category c WHERE c.name=:name")
    List<Category> findByName(@Param("name") String name);

}
