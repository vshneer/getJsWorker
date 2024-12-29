package com.annalabs.getJsWorker.worker;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class JsWorkerTest {

    private static final KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"));

    static {
        kafkaContainer.start();
    }

    private final CountDownLatch latch = new CountDownLatch(1);
    @Autowired
    JsWorker jsWorker;
    private String receivedMessage;

    @DynamicPropertySource
    static void registerKafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
    }

    @Test
    void jsWorkerTest() throws InterruptedException {
        jsWorker.processMessage("destroy.ai");
        boolean messageConsumed = latch.await(2, TimeUnit.SECONDS);
        assertTrue(messageConsumed, "Message was not consumed in time");

    }

    @KafkaListener(topics = "${kafka.topics.js}", groupId = "${kafka.groups.getjs}")
    public void listen(String message) {
        this.receivedMessage = message;
        latch.countDown();
    }
}