package edu.esipe.i3.ezipflix.frontend;

import com.fasterxml.jackson.core.JsonProcessingException;
import edu.esipe.i3.ezipflix.frontend.data.services.VideoConversion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * Created by Gilles GIRAUD gil on 11/4/17.
 */

@SpringBootApplication
@RestController
@EnableWebSocket
public class VideoDispatcher implements WebSocketConfigurer {


    private static final Logger LOGGER = LoggerFactory.getLogger(VideoDispatcher.class);
    @Autowired VideoConversion videoConversion;


    public static void main(String[] args) throws Exception {
        SpringApplication.run(VideoDispatcher.class, args);
    }

    // ┌───────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
    // │ REST Resources                                                                                                │
    // └───────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
    @RequestMapping(method = RequestMethod.POST,
                    value = "/convert",
                    consumes = MediaType.APPLICATION_JSON_VALUE,
                    produces = MediaType.APPLICATION_JSON_VALUE)
    public ConversionResponse requestConversion(@RequestBody ConversionRequest request) throws JsonProcessingException {
        LOGGER.info("File = {}", request.getPath());
        final ConversionResponse response = new ConversionResponse();
        LOGGER.info("UUID = {}", response.getUuid().toString());
        videoConversion.save(request, response);
        return response;
    }

    @Bean
    public WebSocketHandler videoStatusHandler() {
        return new VideoStatusHandler();
    }

    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        webSocketHandlerRegistry.addHandler(videoStatusHandler(), "/video_status");
    }

}
