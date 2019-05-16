package org.udg.pds.springtodo.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.udg.pds.springtodo.controller.exceptions.ServiceException;
import org.udg.pds.springtodo.entity.Game;
import org.udg.pds.springtodo.entity.Post;
import org.udg.pds.springtodo.entity.User;
import org.udg.pds.springtodo.entity.UserValoration;
import org.udg.pds.springtodo.repository.UserRepository;
import org.udg.pds.springtodo.repository.UserValorationRepository;

import java.util.*;

@Service
public class UserService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private UserValorationRepository userValorationRepository;

  @Autowired
  private GameService gameService;

  @Autowired
  private PostService postService;

  public UserRepository crud() {
    return userRepository;
  }

  public User matchPassword(String username, String password) {

    List<User> uc = userRepository.findByName(username);

    if (uc.size() == 0) throw new ServiceException("User does not exists");

    User u = uc.get(0);

    if (u.getPassword().equals(password))
      return u;
    else
      throw new ServiceException("Password does not match");
  }

  public User register(String username, String email, String password) {

   List<User> uEmail = userRepository.findByEmail(email);
    if (uEmail.size() > 0)
      throw new ServiceException("Email already exist");


    List<User> uUsername = userRepository.findByName(username);
    if (uUsername.size() > 0)
      throw new ServiceException("Username already exists");

    User nu = new User(username, email, password);
    userRepository.save(nu);
    return nu;
  }

  public User getUser(Long id) {
    Optional<User> uo = userRepository.findById(id);
    if (uo.isPresent())
      return uo.get();
    else
      throw new ServiceException(String.format("User with id = % dos not exists", id));
  }

  public User getUserProfile(long id) {
    User u = this.getUser(id);
    //for (Task t : u.getTasks())
    //    t.getTags();
    return u;
  }

  @Transactional
  public void addGame(Long userId, Long gameId) {
      User u = this.getUser(userId);

      if (u.getId() != userId)
          throw new ServiceException(("This user is not in the DB"));

      try {
          Optional<Game> o = gameService.crud().findById(gameId);
          if (o.isPresent()){
              if(!u.getGames().contains(o.get())) {
                  u.addGame(o.get());
                  o.get().addUser(u);
              }
          }
          else
              throw new ServiceException("Game does not exists");
      } catch (Exception ex) {
          // Very important: if you want that an exception reaches the EJB caller, you have to throw an ServiceException
          // We catch the normal exception and then transform it in a ServiceException
          throw new ServiceException(ex.getMessage());
      }
  }

    @Transactional
    public void deleteGame(Long userId, Long gameId){
        User user = this.getUser(userId);

        if (user.getId() != userId)
            throw new ServiceException(("This user is not in the DB"));

        for(Game i : user.getGames()){
            if(i.getId() == gameId){
                user.getGames().remove(i);
                break;
            }
        }
    }

    public Collection<Post> getPosts(Long userId){
        User user = this.getUser(userId);

        if (user.getId() != userId)
            throw new ServiceException(("This user is not in the DB"));

        return user.getPosts();
    }

    @Transactional
    public void followAPost(Long userId, Long postId){
        User user = this.getUser(userId);

        if (user.getId() != userId)
            throw new ServiceException(("This user is not in the DB"));

        Post post = postService.getPost(postId);

        if (post.getId() != postId)
            throw new ServiceException(("This post is not in the DB"));

        if (userId == post.getUser().getId())
            throw new ServiceException("You cannot subscribe to your own Post");

        if(!user.getFollowedPosts().contains(post)){
            user.addPostFollowing(post);
            post.addUserFollowing(user);
        }

        if(post.getUser().getToken() != null) {
            Message message = Message.builder()
                    .putData("title", user.getName())
                    .putData("body", "has followed your post!")
                    .putData("postID", post.getId().toString())
                    .putData("gameID", post.getGame().getId().toString())
                    .setToken(post.getUser().getToken())
                    .build();

            try {
                String response = FirebaseMessaging.getInstance().send(message);
            } catch (FirebaseMessagingException e) {
                e.printStackTrace();
            }
        }
    }

    @Transactional
    public void unfollowAPost(Long userId, Long postId){
        User user = this.getUser(userId);

        if (user.getId() != userId)
            throw new ServiceException(("This user is not in the DB"));

        Post post = postService.getPost(postId);

        if (post.getId() != postId)
            throw new ServiceException(("This post is not in the DB"));

        user.removePostFollowing(post);
        post.removeUserFollowing(user);
    }

    @Transactional
    public void updateProfile(Long userId, String name, String description, String image){
        User user = this.getUser(userId);

        if (user.getId() != userId)
            throw new ServiceException(("This user is not in the DB"));

        if(name!="") user.setName(name);
        if(description!="") user.setDescription(description);
        if(image!="") user.setImage(image);
    }

    @Transactional
    public void addToken(Long userId, String token){
        User user = this.getUser(userId);
        if (user.getId() != userId)
            throw new ServiceException(("This user is not in the DB"));
        
        user.setToken(token);
    }

  
    public Collection<Post> getPostsFollowing(Long userId){
        User user = this.getUser(userId);
        if (user.getId() != userId)
            throw new ServiceException(("This user is not in the DB"));
      
        return user.getFollowedPosts();
    }

    @Transactional
    public void updateValoration(Long loggedUserId, Long userId, double valoration) {
        if(loggedUserId==userId) throw new ServiceException("You cannot valorate yourself");

        User loggedUser = this.getUser(loggedUserId);
        if (loggedUser.getId() != loggedUserId)
            throw new ServiceException(("This user is not in the DB"));

        User user = this.getUser(userId);
        if (user.getId() != userId)
            throw new ServiceException(("This user is not in the DB"));


        boolean isNewValoration=true;
        double newValoration = 0;

        for(UserValoration uv: user.getUserValorated()){
            if(uv.getUserValorating()==loggedUser && uv.getUserValorated()==user){
                uv.setValoration(valoration);
                isNewValoration=false;
            }
            newValoration += uv.getValoration();
        }
        if(isNewValoration){
            UserValoration uv = new UserValoration(loggedUser,user,valoration);
            loggedUser.addUserValorating(uv);
            user.addUserValorated(uv);
            newValoration += valoration;
        }

        newValoration/=user.getUserValorated().size();
        user.setValoration(newValoration);
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = this.getUser(userId);

        if (user.getId() != userId)
            throw new ServiceException(("This user is not in the DB"));

        //Own Posts
        ArrayList<Post> ownPosts = new ArrayList<>(user.getPosts());
        for(Post post: ownPosts){
            postService.deletePost(post.getId(),user.getId());
            postService.crud().deleteById(post.getId());
        }

        //Posts subscribed
        ArrayList<Post> followedPosts = new ArrayList<>(user.getFollowedPosts());
        for(Post post: followedPosts){
            user.removePostFollowing(post);
            post.removeUserFollowing(user);
        }

        //Games subscribed
        ArrayList<Game> games = new ArrayList<>(user.getGames());
        for(Game game: games){
            user.removeGame(game);
            game.removeUser(user);
        }

        ArrayList<UserValoration> userValorated = new ArrayList<>(user.getUserValorated());
        for(UserValoration uv : userValorated){
            user.removeUserValorated(uv);
            uv.getUserValorating().removeUserValorating(uv);
            userValorationRepository.deleteById(uv.getId());
        }

        ArrayList<UserValoration> userValorating = new ArrayList<>(user.getUserValorating());
        for(UserValoration uv : userValorating){
            user.removeUserValorating(uv);
            uv.getUserValorated().removeUserValorated(uv);
            userValorationRepository.deleteById(uv.getId());
        }

    }
}
