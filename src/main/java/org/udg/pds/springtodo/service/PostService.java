package org.udg.pds.springtodo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.udg.pds.springtodo.controller.exceptions.ServiceException;
import org.udg.pds.springtodo.entity.Game;
import org.udg.pds.springtodo.entity.Post;
import org.udg.pds.springtodo.entity.User;
import org.udg.pds.springtodo.repository.PostRepository;

import java.util.Date;
import java.util.Optional;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private GameService gameService;

    @Autowired
    private UserService userService;

    public PostRepository crud() { return postRepository; }

    @Transactional
    public Post createPost(String title,Boolean active, String description,Long userId, Long gameId){
        Game g = gameService.getGame(gameId);
        User u = userService.getUser(userId);
        Post np = new Post(title,active,description,g,u);
        Date date = new Date();
        g.addPost(np);
        g.setDataLastPost(date);
        u.addPost(np);
        postRepository.save(np);
        return np;
    }

    public Post getPost(Long id) {
        Optional<Post> post = postRepository.findById(id);
        if (post.isPresent())
            return post.get();
        else
            throw new ServiceException(String.format("Post with id = %d not exists", id));
    }

    @Transactional
    public void changeState(Long postId, Long userId){
        Post post = this.getPost(postId);

        if (post.getId() != postId)
            throw new ServiceException(("This post is not in the DB"));

        if(post.getUserId()!=userId)
            throw new ServiceException("This user is not the owner of this post");

        post.setActive(!post.getActive());
    }
}
