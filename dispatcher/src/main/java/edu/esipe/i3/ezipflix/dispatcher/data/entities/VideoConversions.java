package edu.esipe.i3.ezipflix.dispatcher.data.entities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Gilles GIRAUD gil on 11/4/17.
 */
@Document(collection = "video_conversions")
public class VideoConversions {
    @Id
    private String uuid;
    private String originPath;
    private String format;
    private int progression;


    public VideoConversions() {
    }

    public VideoConversions(String uuid, String originPath, String format, int progression) {
        this.uuid = uuid;
        this.originPath = originPath;
        this.format = format;
        this.progression = progression;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getOriginPath() {
        return originPath;
    }

    public void setOriginPath(String originPath) {
        this.originPath = originPath;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public int getProgression() {
        return progression;
    }

    public void setProgression(int progression) {
        this.progression = progression;
    }

    public String toJson() throws JsonProcessingException {
        final ObjectMapper _mapper = new ObjectMapper();
        final Map<String, String> _map = new HashMap<>();
        _map.put("id", uuid);
        _map.put("originPath", originPath);
        _map.put("format", format);
        byte [] _bytes = _mapper.writeValueAsBytes(_map);
        return new String(_bytes);
    }
}
