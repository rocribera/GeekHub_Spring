package org.udg.pds.springtodo.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.udg.pds.springtodo.controller.exceptions.ServiceException;
import org.udg.pds.springtodo.entity.Message;
import org.udg.pds.springtodo.entity.User;
import org.udg.pds.springtodo.entity.UserMessages;
import org.udg.pds.springtodo.repository.UserMessagesRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class MessageService {

    @Autowired
    private UserMessagesRepository userMessagesRepository;

    @Autowired
    private UserService userService;

    public UserMessagesRepository crud() { return  userMessagesRepository; }

    public UserMessages getUserMessage(Long user1Id, Long user2Id){
        User user1 = userService.getUser(user1Id);
        if (user1.getId() != user1Id)
            throw new ServiceException(("This user is not in the DB"));
        User user2 = userService.getUser(user2Id);
        if (user2.getId() != user2Id)
            throw new ServiceException(("This user is not in the DB"));
        int index = -1;
        int i = 0;
        while(index==-1 && i<user1.getChatsUser1().size()){
            if(user1.getChatsUser1().get(i).compare(user1,user2)) index = i;
            i++;
        }
        if(index!=-1) return user1.getChatsUser1().get(index);
        i=0;
        while(index==-1 && i<user1.getChatsUser2().size()){
            if(user1.getChatsUser2().get(i).compare(user1,user2)) index = i;
            i++;
        }
        if(index!=-1) return user1.getChatsUser2().get(index);
        return null;
    }

    public List<Message> getMessages(Long user1Id, Long user2Id){
        UserMessages um = this.getUserMessage(user1Id,user2Id);
        if(um!=null) return um.getMessages();
        return new ArrayList<>();
    }

    @Transactional
    public int addNewMessage(Long senderId, Long receiverId, String message, String createdAt){
        User sender = userService.getUser(senderId);
        if (sender.getId() != senderId)
            throw new ServiceException(("This user is not in the DB"));
        User receiver = userService.getUser(receiverId);
        if (receiver.getId() != receiverId)
            throw new ServiceException(("This user is not in the DB"));

        UserMessages um = this.getUserMessage(senderId,receiverId);
        if(um!=null){
            if(um.isActive()) um.addMessage(new Message(message,createdAt,senderId,um));
            else return 1;
        }
        else{
            throw new ServiceException("Chat not opened");
        }

        if(receiver.getToken() != null) {
            com.google.firebase.messaging.Message notifMessage = com.google.firebase.messaging.Message.builder()
                    .putData("chat", "1")
                    .putData("title", sender.getName())
                    .putData("body", message)
                    .putData("userID", sender.getId().toString())
                    .putData("myID", receiver.getId().toString())
                    .setToken(receiver.getToken())
                    .build();

            try {
                String response = FirebaseMessaging.getInstance().send(notifMessage);
            } catch (FirebaseMessagingException e) {
                e.printStackTrace();
            }
        }

        return 0;
    }

    public List<UserMessages> getChats(Long userId, int type) { //Type: 0 all, 1 open, 2 closed
         User user = userService.getUser(userId);
        if (user.getId() != userId)
            throw new ServiceException(("This user is not in the DB"));

        List<UserMessages> userMessages = new ArrayList<>();
        if(type==0){
            userMessages.addAll(user.getChatsUser1());
            userMessages.addAll(user.getChatsUser2());
        }
        else if(type==1 || type==2) {
            for(UserMessages um : user.getChatsUser1()){
                if(type==1 && um.isActive()) userMessages.add(um);
                else if(type==2 && !um.isActive()) userMessages.add(um);
            }
            for(UserMessages um : user.getChatsUser2()){
                if(type==1 && um.isActive()) userMessages.add(um);
                else if(type==2 && !um.isActive()) userMessages.add(um);
            }
        }

        return userMessages;
    }

    @Transactional
    public void closeChat(Long myId, Long userId) {
        User myUser = userService.getUser(myId);
        if (myUser.getId() != myId)
            throw new ServiceException(("This user is not in the DB"));
        User otherUser = userService.getUser(userId);
        if (otherUser.getId() != userId)
            throw new ServiceException(("This user is not in the DB"));

        UserMessages um = this.getUserMessage(myId,userId);
        if(um!=null){
            um.setActive(false);
        } else {
            throw new ServiceException("User does not have any chat active with "+otherUser.getName());
        }

        if(otherUser.getToken() != null) {
            com.google.firebase.messaging.Message notifMessage = com.google.firebase.messaging.Message.builder()
                    .putData("chat", "2")
                    .putData("title", myUser.getName())
                    .putData("body", "has closed your chat")
                    .putData("userId",myUser.getId().toString())
                    .setToken(otherUser.getToken())
                    .build();

            try {
                String response = FirebaseMessaging.getInstance().send(notifMessage);
            } catch (FirebaseMessagingException e) {
                e.printStackTrace();
            }
        }
    }

    @Transactional
    public UserMessages openChat(Long myId, Long userId) {
        User myUser = userService.getUser(myId);
        if (myUser.getId() != myId)
            throw new ServiceException(("This user is not in the DB"));
        User otherUser = userService.getUser(userId);
        if (otherUser.getId() != userId)
            throw new ServiceException(("This user is not in the DB"));

        UserMessages um = this.getUserMessage(myId,userId);
        if(um!=null){
            if(um.getBlock()==0) um.setActive(true);
            return um;
        }
        else{
            um = new UserMessages(myUser,otherUser);
            myUser.addNewChatUser1(um);
            otherUser.addNewChatUser2(um);
            return um;
        }
    }
}

