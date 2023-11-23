package org.example.quarkuskafkaprotobuf.pact;

import au.com.dius.pact.consumer.dsl.PactBuilder;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.consumer.junit5.ProviderType;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.V4Interaction;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.example.kafkaprotobuf.Address;
import com.example.kafkaprotobuf.Person;
import com.example.kafkaprotobuf.PhoneNumber;
import com.google.protobuf.InvalidProtocolBufferException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "quarkusKafkaProtoProducer", providerType = ProviderType.ASYNCH, pactVersion = PactSpecVersion.V4)
class QuarkusKafkaProtoConsumerContractTest {

    @Pact(consumer = "quarkusKafkaProtoConsumer", provider = "quarkusKafkaProtoProducer")
    public V4Pact quarkusKafkaProtoConsumerPact(PactBuilder builder) {
        return builder
            .usingPlugin("protobuf")
            .expectsToReceive("message with personal info", "core/interaction/message")
            .with(Map.of(
                "message.contents", Map.of(
                    "pact:proto", PactBuilder.filePath("src/main/proto/Person.proto"),
                    "pact:message-type", "Person",
                    "pact:content-type", "application/protobuf",
                    "name", "matching(regex, '^[A-Z]*$', 'JOHN')",
                    "age", "matching(integer, 50)",
                    "email", "matching(equalTo, 'abc@gmail.com')",
                    "address", Map.of(
                        "city", "matching(regex, '^[A-Z]*$', 'MY CITY')",
                        "state", "matching(regex, '^[A-Z]*$', 'TELANGANA')",
                        "zip_code", "matching(regex, '^[1-9]{1}[0-9]{5}$', '500001')"
                    ),
                    "phone_numbers", Map.of(
                        "pact:match", "eachValue(matching($'phone_number'))",
                        "phone_number", Map.of(
                            "number", "matching(regex, '^[6-9]{1}[0-9]{9}$', '9999888870')",
                            "type", "matching(type, 'MOBILE')"
                        )
                    ),
                    "grades", Map.of(
                        "pact:match", "eachKey(matching(regex, '^[a-z]*$', 'math')), eachValue(matching(integer, 10))"
                    )
                )
            ))
            .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "quarkusKafkaProtoConsumerPact")
    void validatePersonMessage(V4Interaction.AsynchronousMessage message) throws InvalidProtocolBufferException {
        Person person = Person.parseFrom(message.getContents().getContents().getValue());
        assertEquals("JOHN", person.getName());
        assertEquals(50, person.getAge());
        assertFalse(person.getEmail().isEmpty());
        assertEquals("abc@gmail.com", person.getEmail());

        Address address = person.getAddress();
        assertEquals("MY CITY", address.getCity());
        assertEquals("TELANGANA", address.getState());
        assertEquals("500001", address.getZipCode());

        List<PhoneNumber> phoneNumbersList = person.getPhoneNumbersList();
        assertEquals(1, phoneNumbersList.size());
        phoneNumbersList.forEach(phoneNumber -> {
            assertEquals("9999888870", phoneNumber.getNumber());
            assertEquals(PhoneNumber.Type.MOBILE, phoneNumber.getType());
        });
    }
}
