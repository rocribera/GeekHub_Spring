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

    public List<Message> getMessages(Long user1Id, Long user2Id){
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
        if(index!=-1) return user1.getChatsUser1().get(index).getMessages();
        i=0;
        while(index==-1 && i<user1.getChatsUser2().size()){
            if(user1.getChatsUser2().get(i).compare(user1,user2)) index = i;
            i++;
        }
        if(index!=-1) return user1.getChatsUser2().get(index).getMessages();
        return new ArrayList<>();
    }

    @Transactional
    public void addNewMessage(Long senderId, Long receiverId, String message, String createdAt){
        User sender = userService.getUser(senderId);
        if (sender.getId() != senderId)
            throw new ServiceException(("This user is not in the DB"));

        if(senderId==receiverId) throw new ServiceException("You cannot chat with yourself");
        User receiver = userService.getUser(receiverId);
        if (receiver.getId() != receiverId)
            throw new ServiceException(("This user is not in the DB"));

        int index = -1;
        int i = 0;
        while(index==-1 && i<sender.getChatsUser1().size()){
            if(sender.getChatsUser1().get(i).compare(sender,receiver)) index = i;
            i++;
        }

        if(index!=-1) sender.getChatsUser1().get(index).addMessage(new Message(message,createdAt,sender.getId(),sender.getChatsUser1().get(index)));
        else {
            i = 0;
            while (index == -1 && i < sender.getChatsUser2().size()) {
                if (sender.getChatsUser2().get(i).compare(sender, receiver)) index = i;
                i++;
            }
            if (index != -1)
                sender.getChatsUser2().get(index).addMessage(new Message(message, createdAt, sender.getId(), sender.getChatsUser2().get(index)));
            else{
                //Això s'ha de fer quan s'obri un chat, no aquí
                UserMessages um = new UserMessages(sender,receiver);
                um.addMessage(new Message(message,createdAt,sender.getId(),um));
                sender.addNewChatUser1(um);
                receiver.addNewChatUser2(um);
            }
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
    }

    public List<UserMessages> getChats(Long userId,int type) { //Type: 0 all, 1 open, 2 closed
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
        int index = -1;
        int i=0;
        while(index==-1 && i<myUser.getChatsUser1().size()){
            if(myUser.getChatsUser1().get(i).compare(myUser,otherUser)) index=i;
            i++;
        }
        if(index!=-1){
            myUser.getChatsUser1().get(index).setActive(false);
        }
        else{
            i=0;
            while(index==-1 && i<myUser.getChatsUser2().size()){
                if(myUser.getChatsUser2().get(i).compare(myUser,otherUser)) index=i;
                i++;
            }
            if(index!=-1){
                myUser.getChatsUser2().get(index).setActive(false);
            }
            else{
                throw new ServiceException("User does not have any chat active with "+otherUser.getName());
            }
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

}
