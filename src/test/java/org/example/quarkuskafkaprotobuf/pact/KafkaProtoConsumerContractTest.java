package org.example.quarkuskafkaprotobuf.pact;

import au.com.dius.pact.consumer.dsl.PactBuilder;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.consumer.junit5.ProviderType;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.V4Interaction;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.example.kafkaprotobuf.Person;
import com.google.protobuf.InvalidProtocolBufferException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "quarkusKafkaProtoProducer", providerType = ProviderType.ASYNCH, pactVersion = PactSpecVersion.V4)
class KafkaProtoConsumerContractTest {

    @Pact(consumer = "quarkusKafkaProtoConsumer", provider = "quarkusKafkaProtoProducer")
    public V4Pact quarkusKafkaProtoConsumerPact(PactBuilder builder) {
        return builder
            .usingPlugin("protobuf")
            .expectsToReceive("message with person details", "core/interaction/message")
            .with(Map.of(
                "message.contents", Map.of(
                    "pact:proto", PactBuilder.filePath("src/main/proto/Person.proto"),
                    "pact:message-type", "Person",
                    "pact:content-type", "application/protobuf",
                    "name", "matching(equalTo, 'Pavan')",
                    "age", "matching(equalTo, 50)"
                )
            ))
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "quarkusKafkaProtoConsumerPact")
    void validatePersonMessage(V4Interaction.AsynchronousMessage message) throws InvalidProtocolBufferException {
        Person person = Person.parseFrom(message.getContents().getContents().getValue());
        assertEquals("Pavan", person.getName());
        assertEquals(50, person.getAge());
    }
}
