package org.udg.pds.springtodo.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.udg.pds.springtodo.entity.Post;
import org.udg.pds.springtodo.entity.User;

import java.io.IOException;
import java.util.Iterator;

public class JsonPostSerializer extends JsonSerializer<Post> {

    @Override
    public void serialize(Post post, JsonGenerator gen, SerializerProvider provider)
            throws IOException, JsonProcessingException {
        gen.writeStartObject();
        gen.writeNumberField("id",post.getId());
        gen.writeNumberField("userId",post.getUser().getId());
        gen.writeStringField("username",post.getUser().getName());
        gen.writeStringField("title",post.getTitle());
        gen.writeBooleanField("active",post.getActive());
        gen.writeStringField("description",post.getDescription());
        gen.writeArrayFieldStart("followers");
        Iterator<User> itUser = post.getFollowers().iterator();
        while(itUser.hasNext()){
            User u = itUser.next();
            gen.writeStartObject();
            gen.writeNumberField("id",u.getId());
            gen.writeStringField("name",u.getName());
            gen.writeEndObject();
        }
        gen.writeEndArray();
        gen.writeEndObject();
    }
}
