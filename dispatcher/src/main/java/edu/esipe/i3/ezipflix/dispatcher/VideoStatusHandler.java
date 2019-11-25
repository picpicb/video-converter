package edu.esipe.i3.ezipflix.dispatcher;

import edu.esipe.i3.ezipflix.dispatcher.data.services.VideoConversion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

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
        // pour le uuid récupéré :  mettre à jour la progression de VideoConversion assosié
        /*
        Json à recevoir
        {"uuid": "dsflkjsdf", "progression": 23}
         */
    }
}
