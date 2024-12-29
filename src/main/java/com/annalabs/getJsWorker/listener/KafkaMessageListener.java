package com.annalabs.getJsWorker.listener;

import com.annalabs.getJsWorker.worker.JsWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class KafkaMessageListener {

    private final ExecutorService executorService = Executors.newFixedThreadPool(10); // Thread pool for async processing
    @Autowired
    private JsWorker jsWorker;

    @KafkaListener(topics = {"${kafka.topics.domain}", "${kafka.topics.subdomain}"}, groupId = "${kafka.groups.getjs}")
    public void listen(String message) {
        executorService.submit(() -> jsWorker.processMessage(message));
    }
}
