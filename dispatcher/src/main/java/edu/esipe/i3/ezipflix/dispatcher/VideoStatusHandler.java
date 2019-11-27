package edu.esipe.i3.ezipflix.dispatcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.esipe.i3.ezipflix.dispatcher.data.entities.ConversionStatus;
import edu.esipe.i3.ezipflix.dispatcher.data.services.VideoConversion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.swing.*;
import java.io.IOException;

/**
 * Created by Gilles GIRAUD gil on 1/22/18.
 */

public class VideoStatusHandler extends TextWebSocketHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(VideoStatusHandler.class);
    private VideoConversion videoConversion;
    public VideoStatusHandler(VideoConversion videoConversion) {
        this.videoConversion = videoConversion;
    }


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        LOGGER.info("Session opened = {}", session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        LOGGER.info("Status = {}", message.getPayload());
        //{"progress": 6, "id": "1ed1f172-b209-45c2-8682-e6952a4a6625"}
        try {
            ObjectMapper mapper = new ObjectMapper();
            ConversionStatus conversionStatus = mapper.readValue(message.getPayload(), ConversionStatus.class);
            videoConversion.updateProgress(conversionStatus);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
