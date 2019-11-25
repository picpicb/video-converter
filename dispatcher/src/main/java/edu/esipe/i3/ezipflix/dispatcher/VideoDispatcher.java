package edu.esipe.i3.ezipflix.dispatcher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.cloud.storage.BlobId;
import edu.esipe.i3.ezipflix.dispatcher.data.entities.VideoConversions;
import edu.esipe.i3.ezipflix.dispatcher.data.entities.VideoFile;
import edu.esipe.i3.ezipflix.dispatcher.data.services.VideoConversion;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.List;

/**
 * Created by Gilles GIRAUD gil on 11/4/17.
 */

@SpringBootApplication
@RestController
@EnableWebSocket
@EnableSwagger2
@Api(tags = "Video")
public class VideoDispatcher implements WebSocketConfigurer {

    private static final Logger LOGGER = LoggerFactory.getLogger(VideoDispatcher.class);
    @Autowired VideoConversion videoConversion;

    public static void main(String[] args) throws Exception {
        SpringApplication.run(VideoDispatcher.class, args);
    }

    @Bean
    public WebSocketHandler videoStatusHandler() {
        return new VideoStatusHandler();
    }

    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        webSocketHandlerRegistry.addHandler(videoStatusHandler(), "/video_status").setAllowedOrigins("*");
    }

    // ┌───────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
    // │ REST Resources                                                                                                │
    // └───────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
    @RequestMapping(method = RequestMethod.POST,value = "/convert", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Create conversion")
    public ConversionResponse requestConversion(@RequestBody ConversionRequest request) throws JsonProcessingException {
        LOGGER.info("File = {}", request.getPath());
        final ConversionResponse response = new ConversionResponse();
        LOGGER.info("UUID = {}", response.getUuid().toString());
        videoConversion.save(request, response);
        return response;
    }
    @GetMapping(value = "/files",produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get all stored files")
    public List<VideoFile> getAllFiles() {
        return videoConversion.getAllFiles();
    }

    @GetMapping(value = "/conversions",produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get all conversions")
    public List<VideoConversions> getAllVideoConversions() {
        return videoConversion.getAllVideoConversions();
    }

    @RequestMapping(value = "/files/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "Delete a file by ID")
    @ResponseBody
    public void deleteCityById(@RequestBody VideoFile file) {
        this.videoConversion.deleteBlobId(file);
    }


}