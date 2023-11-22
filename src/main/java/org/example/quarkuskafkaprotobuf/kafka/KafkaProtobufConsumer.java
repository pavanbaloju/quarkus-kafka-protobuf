package org.example.quarkuskafkaprotobuf.kafka;

import com.example.kafkaprotobuf.Person;
import io.smallrye.reactive.messaging.annotations.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
public class KafkaProtobufConsumer {

    @Incoming("test-topic")
    @Blocking
    public void process(Person person) {
        System.out.println("Message received from Kafka " + person);
    }

}
