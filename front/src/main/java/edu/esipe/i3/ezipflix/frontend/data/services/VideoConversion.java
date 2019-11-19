package edu.esipe.i3.ezipflix.frontend.data.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import edu.esipe.i3.ezipflix.frontend.ConversionRequest;
import edu.esipe.i3.ezipflix.frontend.ConversionResponse;
import edu.esipe.i3.ezipflix.frontend.data.entities.VideoConversions;
import edu.esipe.i3.ezipflix.frontend.data.repositories.VideoConversionRepository;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.pubsub.core.PubSubTemplate;
import org.springframework.stereotype.Service;

/**
 * Created by Gilles GIRAUD gil on 11/4/17.
 */
@Service
public class VideoConversion {

    @Autowired VideoConversionRepository videoConversionRepository;

    @Autowired PubSubTemplate pubSubTemplate;


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




}
