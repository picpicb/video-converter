package edu.esipe.i3.ezipflix.dispatcher.data.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.cloud.storage.*;
import edu.esipe.i3.ezipflix.dispatcher.ConversionRequest;
import edu.esipe.i3.ezipflix.dispatcher.ConversionResponse;
import edu.esipe.i3.ezipflix.dispatcher.data.entities.ConversionStatus;
import edu.esipe.i3.ezipflix.dispatcher.data.entities.VideoConversions;
import edu.esipe.i3.ezipflix.dispatcher.data.entities.VideoFile;
import edu.esipe.i3.ezipflix.dispatcher.data.repositories.VideoConversionRepository;
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
    private ArrayList<VideoConversions> conversions;

    public VideoConversion() throws IOException {
        this.storage = StorageOptions.getDefaultInstance().getService();
        this.bucket = storage.get("eco-groove-259413.appspot.com");
        conversions = new ArrayList<>();
    }

    public void save(
                final ConversionRequest request,
                final ConversionResponse response) throws JsonProcessingException {

        final VideoConversions conversion = new VideoConversions(response.getUuid().toString(), request.getPath().toString(), request.getFormat(),0);
        conversions.add(conversion);
        videoConversionRepository.save(conversion);
        final Message message = new Message(conversion.toJson().getBytes(), new MessageProperties());
        this.pubSubTemplate.publish("conversion-queue", conversion.toJson());
    }

    public List<VideoFile> getAllFiles() {
        List<VideoFile> files = new ArrayList<>();
        for (Blob blob : bucket.list().iterateAll()) {
            files.add(new VideoFile(blob.getBucket(), blob.getBucket()+"/"+blob.getName(), blob.getName(),blob.getSize().toString(),blob.getContentType()));
        }
        return files;
    }

    public List<VideoConversions> getAllVideoConversions(){
        return videoConversionRepository.findAll();
    }

    public void deleteBlobId(VideoFile file) {
        BlobId blobId = BlobId.of(file.getBucket(),file.getName());
        boolean deleted = storage.delete(blobId);
    }

    public ArrayList<VideoConversions> getRunningConversions(){
        return conversions;
    }

    public void updateProgress(ConversionStatus status){
        for (VideoConversions v : conversions) {
            if(v.getUuid().equals(status.getUuid())){
                v.setProgression(status.getPercentage());
            }
        }
    }

}
