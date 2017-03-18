package app.packman.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import org.joda.time.LocalDateTime;

import java.io.IOException;

;

/**
 * Created by sujaysudheendra on 11/22/15.
 */
public class JsonDateDeserializer extends JsonDeserializer<LocalDateTime> {
    @Override
    public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        return new LocalDateTime(jsonParser.getText());
    }
}
