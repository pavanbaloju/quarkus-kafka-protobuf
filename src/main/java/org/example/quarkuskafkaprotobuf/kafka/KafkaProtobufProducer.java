package org.example.quarkuskafkaprotobuf.kafka;

import com.example.kafkaprotobuf.Person;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import java.util.stream.IntStream;

import static java.lang.System.out;

@ApplicationScoped
public class KafkaProtobufProducer {

    @Inject
    @Channel("test-topic-out")
    Emitter<Person> emitter;

    void onStart(@Observes StartupEvent ev) {
        IntStream.range(1, 6)
            .mapToObj(i -> Person.newBuilder().setName("Person " + i).setAge(20).build())
            .forEach(person -> {
                emitter.send(person);
                out.println("Produced message: " + person);
            });
    }
}
