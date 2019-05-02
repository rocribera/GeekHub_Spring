package org.udg.pds.springtodo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
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

    @DeleteMapping(path="/{id}")
    public String deletePost(HttpSession session,
                             @PathVariable("id") Long postId) {

        Long userId = getLoggedUser(session);

        postService.deletePost(postId,userId);
        postService.crud().deleteById(postId);
        return BaseController.OK_MESSAGE;
    }
}
