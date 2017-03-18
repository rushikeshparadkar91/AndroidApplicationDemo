package app.packman.utils;

import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import org.joda.time.LocalDateTime;

import java.io.IOException;

/**
 * Created by sujaysudheendra on 11/22/15.
 */
public class JsonDateSerializer extends JsonSerializer<LocalDateTime> {


    @Override
    public void serialize(LocalDateTime localDateTime, com.fasterxml.jackson.core.JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, com.fasterxml.jackson.core.JsonProcessingException {
        jsonGenerator.writeString(String.valueOf(localDateTime));
    }
}
