package org.udg.pds.springtodo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.udg.pds.springtodo.service.CategoryService;

@RestController
public class CategoryController extends BaseController{

    @Autowired
    CategoryService categoryService;
}
