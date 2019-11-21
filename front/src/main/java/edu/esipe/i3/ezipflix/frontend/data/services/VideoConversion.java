package edu.esipe.i3.ezipflix.frontend.data.services;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import edu.esipe.i3.ezipflix.frontend.ConversionRequest;
import edu.esipe.i3.ezipflix.frontend.ConversionResponse;
import edu.esipe.i3.ezipflix.frontend.data.entities.VideoConversions;
import edu.esipe.i3.ezipflix.frontend.data.entities.VideoFile;
import edu.esipe.i3.ezipflix.frontend.data.repositories.VideoConversionRepository;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.pubsub.core.PubSubTemplate;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gilles GIRAUD gil on 11/4/17.
 */
@Service
public class VideoConversion {

    @Autowired VideoConversionRepository videoConversionRepository;

    @Autowired PubSubTemplate pubSubTemplate;
    private Storage storage;
    private Bucket bucket;

    public VideoConversion() throws IOException {
        this.storage = StorageOptions.getDefaultInstance().getService();
        this.bucket = storage.get("eco-groove-259413.appspot.com");
    }

    public void save(
                final ConversionRequest request,
                final ConversionResponse response) throws JsonProcessingException {

        final VideoConversions conversion = new VideoConversions(
                                                    response.getUuid().toString(),
                                                    request.getPath().toString(),
                                                    request.getTargetPath().toString());

        videoConversionRepository.save(conversion);
        final Message message = new Message(conversion.toJson().getBytes(), new MessageProperties());
        this.pubSubTemplate.publish("conversion-queue", conversion.toJson());
    }


    public List<VideoFile> getAllFiles() {
        List<VideoFile> files = new ArrayList<>();
        for (Blob blob : bucket.list().iterateAll()) {
            files.add(new VideoFile(blob.getBlobId().toString(),blob.getName(),blob.getSize().toString(),blob.getContentType()));
        }
        return files;
    }

    public List<VideoConversions> getAllVideoConversions(){
        return videoConversionRepository.findAll();
    }
}
