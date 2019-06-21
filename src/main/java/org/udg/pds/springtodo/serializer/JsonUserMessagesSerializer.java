package org.udg.pds.springtodo.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.udg.pds.springtodo.controller.MessageController;
import org.udg.pds.springtodo.entity.User;
import org.udg.pds.springtodo.entity.UserMessages;

import java.io.IOException;

public class JsonUserMessagesSerializer extends JsonSerializer<UserMessages> {
    @Override
    public void serialize(UserMessages userMessages, JsonGenerator gen, SerializerProvider provider)
            throws IOException, JsonProcessingException {
        gen.writeStartObject();
        User myUser,otherUser;
        if(MessageController.actualId==userMessages.getUser1().getId()){
            myUser = userMessages.getUser1();
            otherUser = userMessages.getUser2();
        }
        else if(MessageController.actualId==userMessages.getUser2().getId()){
            myUser = userMessages.getUser2();
            otherUser = userMessages.getUser1();
        }
        else throw new IOException("User not detected");

        gen.writeNumberField("myUserId",myUser.getId());
        gen.writeObjectFieldStart("otherUser");
        gen.writeNumberField("id",otherUser.getId());
        gen.writeStringField("name",otherUser.getName());
        gen.writeStringField("image", otherUser.getImage());
        gen.writeBooleanField("updatedImage", otherUser.getUpdatedImage());
        gen.writeEndObject();


        gen.writeBooleanField("chatActive",userMessages.isActive());
        gen.writeNumberField("blockUser", userMessages.getBlock());
        gen.writeEndObject();
    }
}
