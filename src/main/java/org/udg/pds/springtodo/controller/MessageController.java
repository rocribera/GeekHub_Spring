package org.udg.pds.springtodo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.udg.pds.springtodo.entity.Message;
import org.udg.pds.springtodo.entity.UserMessages;
import org.udg.pds.springtodo.service.MessageService;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RequestMapping(path="/message")
@RestController
public class MessageController extends BaseController {

    @Autowired
    MessageService messageService;

    static public long actualId = 0;

    @GetMapping(path="/me/{id}")
    public List<Message> getMessageWithUser(HttpSession session, @PathVariable("id") Long receiverId) {

        Long senderId = getLoggedUser(session);

        return messageService.getMessages(senderId,receiverId);
    }

    @PostMapping(path="/me/{id}")
    public String addNewMessage(HttpSession session, @PathVariable("id") Long receiverId, @Valid @RequestBody MessageReceived userMessage){
        Long loggedUserId =getLoggedUser(session);

        messageService.addNewMessage(loggedUserId,receiverId,userMessage.message,userMessage.createdAt);
        return BaseController.OK_MESSAGE;
    }

    static class MessageReceived {
        @NotNull
        public String message;
        @NotNull
        public String createdAt;
    }

    @GetMapping(path = "/me")
    public List<UserMessages> getAllChats(HttpSession session){
        Long loggedUserId = getLoggedUser(session);
        actualId = loggedUserId;
        return messageService.getChats(loggedUserId,0);
    }

    @GetMapping(path = "/me/open")
    public List<UserMessages> getChatsOpen(HttpSession session){
        Long loggedUserId = getLoggedUser(session);
        actualId = loggedUserId;
        return messageService.getChats(loggedUserId,1);
    }

    @GetMapping(path = "/me/closed")
    public List<UserMessages> getChatsClosed(HttpSession session){
        Long loggedUserId = getLoggedUser(session);
        actualId = loggedUserId;
        return messageService.getChats(loggedUserId,2);
    }

    @PostMapping(path = "/me/{id}/close")
    public String closeChat(HttpSession session,@PathVariable("id") Long userId){
        Long loggedUserId = getLoggedUser(session);
        messageService.closeChat(loggedUserId,userId);
        return BaseController.OK_MESSAGE;
    }

}
