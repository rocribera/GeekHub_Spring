package org.udg.pds.springtodo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.udg.pds.springtodo.service.PostService;

@RestController
public class PostController extends BaseController{

    @Autowired
    PostService postService;
}
