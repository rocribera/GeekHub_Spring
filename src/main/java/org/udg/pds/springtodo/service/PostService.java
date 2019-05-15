package org.udg.pds.springtodo.service;

import com.google.firebase.messaging.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.udg.pds.springtodo.controller.exceptions.ServiceException;
import org.udg.pds.springtodo.entity.Game;
import org.udg.pds.springtodo.entity.Post;
import org.udg.pds.springtodo.entity.User;
import org.udg.pds.springtodo.repository.PostRepository;

import java.util.*;

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

        List<User> listUsers = new ArrayList(g.getUsers());
        List<String> registrationTokens = new ArrayList<>();
        for(int i = 0; i < listUsers.size(); i++){
            if(listUsers.get(i).getToken()!=null && listUsers.get(i).getId() != u.getId()) {
                registrationTokens.add(listUsers.get(i).getToken());

                if ((i + 1) % 100 == 0) {
                    MulticastMessage message = MulticastMessage.builder()
                            .putData("title", g.getName())
                            .putData("body", "New Post! : " + title)
                            .putData("postID", np.getId().toString())
                            .putData("gameID", g.getId().toString())
                            .addAllTokens(registrationTokens)
                            .build();
                    try {
                        BatchResponse response = FirebaseMessaging.getInstance().sendMulticast(message);
                    } catch (FirebaseMessagingException e) {
                        e.printStackTrace();
                    }

                    registrationTokens.clear();
                }
            }
        }
        if(registrationTokens.size()>0) {
            MulticastMessage message = MulticastMessage.builder()
                    .putData("title", g.getName())
                    .putData("body", "New Post! : " + title)
                    .putData("postID", np.getId().toString())
                    .putData("gameID", g.getId().toString())
                    .addAllTokens(registrationTokens)
                    .build();
            try {
                BatchResponse response = FirebaseMessaging.getInstance().sendMulticast(message);
            } catch (FirebaseMessagingException e) {
                e.printStackTrace();
            }
        }

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

        if(post.getUser().getId()!=userId)
            throw new ServiceException("This user is not the owner of this post");

        post.setActive(!post.getActive());
    }

    @Transactional
    public void deletePost(Long postId, Long userId){
        Post post = this.getPost(postId);

        if (post.getId() != postId)
            throw new ServiceException(("This post is not in the DB"));

        if(post.getUser().getId()!=userId)
            throw new ServiceException("This user is not the owner of this post");

        Collection<User> users = new ArrayList<User>(post.getFollowers());
        for(User user : users){
            userService.unfollowAPost(user.getId(),postId);
        }
        post.getGame().removePost(post);
        userService.getUser(userId).removeOwnPost(post);
    }
}
