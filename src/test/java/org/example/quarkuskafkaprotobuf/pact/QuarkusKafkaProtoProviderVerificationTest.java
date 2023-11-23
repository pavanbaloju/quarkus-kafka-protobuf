package org.example.quarkuskafkaprotobuf.pact;

import au.com.dius.pact.provider.MessageAndMetadata;
import au.com.dius.pact.provider.PactVerifyProvider;
import au.com.dius.pact.provider.junit5.MessageTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Consumer;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.loader.PactBroker;
import com.example.kafkaprotobuf.Address;
import com.example.kafkaprotobuf.Person;
import com.example.kafkaprotobuf.PhoneNumber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Map;

@Provider("quarkusKafkaProtoProducer")
@Consumer("quarkusKafkaProtoConsumer")
@PactBroker(url = "http://localhost:9292/")
class QuarkusKafkaProtoProviderVerificationTest {

    @BeforeEach
    void before(PactVerificationContext context) {
        System.setProperty("pact.verifier.publishResults", "true");
        System.setProperty("pact_do_not_track", "true");
        context.setTarget(new MessageTestTarget());
    }

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void pactVerificationTestTemplate(PactVerificationContext context) {
        context.verifyInteraction();
    }

    @PactVerifyProvider("message with personal info")
    MessageAndMetadata verifyPersonalInfoMessage() {
        Map<String, Object> metadata = Map.of("content-type", "application/protobuf");
        Person person = Person.newBuilder()
            .setName("JOHN")
            .setAge(50)
            .setEmail("abc@gmail.com")
            .setAddress(Address.newBuilder().setCity("CITY").setState("TELANGANA").setZipCode("500001").build())
            .addAllPhoneNumbers(List.of(PhoneNumber.newBuilder().setNumber("9999888870").setType(PhoneNumber.Type.MOBILE).build()))
            .putAllGrades(Map.of("math", 10))
            .build();

        return new MessageAndMetadata(person.toByteArray(), metadata);
    }
}
