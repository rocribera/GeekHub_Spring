package org.udg.pds.springtodo.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.udg.pds.springtodo.entity.User;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

public class JsonFollowersSerializer extends JsonSerializer<Collection<User>> {

    @Override
    public void serialize(Collection<User> user, JsonGenerator gen, SerializerProvider provider)
            throws IOException, JsonProcessingException {
        gen.writeStartArray();
        Iterator<User> itUser = user.iterator();
        while(itUser.hasNext()){
            User u = itUser.next();
            gen.writeStartObject();;
            gen.writeNumberField("id",u.getId());
            gen.writeStringField("username",u.getName());
            gen.writeEndObject();
        }
        gen.writeEndArray();
    }
}
