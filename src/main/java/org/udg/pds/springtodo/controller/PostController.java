package org.udg.pds.springtodo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.udg.pds.springtodo.entity.Post;
import org.udg.pds.springtodo.service.PostService;

import javax.servlet.http.HttpSession;

@RequestMapping(path="/posts")
@RestController
public class PostController extends BaseController{

    @Autowired
    PostService postService;

    @GetMapping(path="/{id}")
    public Post getPost(HttpSession session, @PathVariable("id") Long postId){
        getLoggedUser(session);
        return postService.getPost(postId);
    }
}
