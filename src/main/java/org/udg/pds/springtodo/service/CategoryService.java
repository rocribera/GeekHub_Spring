package org.udg.pds.springtodo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.udg.pds.springtodo.controller.exceptions.ServiceException;
import org.udg.pds.springtodo.entity.Category;
import org.udg.pds.springtodo.repository.CategoryRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private GameService gameService;

    public CategoryRepository crud() {
        return categoryRepository;
    }

    public Category getCategory(Long id){
        Optional<Category> uo = categoryRepository.findById(id);
        if (uo.isPresent())
            return uo.get();
        else
            throw new ServiceException(String.format("Category with id = % dos not exists", id));
    }

    public Category createCategory(String name) {
        List<Category> uName = categoryRepository.findByName(name);
        if (uName.size() > 0)
            throw new ServiceException("Name already exist");

        Category c = new Category(name);
        categoryRepository.save(c);
        return c;
    }
}
