package edu.esipe.i3.ezipflix.dispatcher;

import com.fasterxml.jackson.core.JsonProcessingException;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gilles GIRAUD gil on 11/4/17.
 */

@SpringBootApplication
@RestController
@EnableWebSocket
@EnableSwagger2
@CrossOrigin(origins = "*")
@Api(tags = "Video")
public class VideoDispatcher implements WebSocketConfigurer {

    private static final Logger LOGGER = LoggerFactory.getLogger(VideoDispatcher.class);
    @Autowired VideoConversion videoConversion;

    public static void main(String[] args) throws Exception {
        SpringApplication.run(VideoDispatcher.class, args);
    }

    @Bean
    public WebSocketHandler videoStatusHandler() {
        return new VideoStatusHandler(videoConversion);
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
    // Envoie la liste des uri + infos de tous les fichiers du Google Storage
    @GetMapping(value = "/files",produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get all stored files")
    public List<VideoFile> getAllFiles() {
        return videoConversion.getAllFiles();
    }

    // Envoie l'historique des conversions
    @GetMapping(value = "/conversions",produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get all conversions")
    public List<VideoConversions> getAllVideoConversions() {
        return videoConversion.getAllVideoConversions();
    }

    @RequestMapping(value = "/files/{file}", method = RequestMethod.DELETE)
    @ApiOperation(value = "Delete a file")
    @ResponseBody
    public void deleteCityById(@RequestBody VideoFile file) {
        this.videoConversion.deleteBlobId(file);
    }

    // Envoie les conversions en cours
    @GetMapping(value = "/running",produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get all conversions")
    public ArrayList<VideoConversions> getRunningConversions() {
        return videoConversion.getRunningConversions();
    }
}
