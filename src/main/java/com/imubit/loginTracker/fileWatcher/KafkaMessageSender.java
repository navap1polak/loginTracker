package com.imubit.loginTracker.fileWatcher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaMessageSender {
    private KafkaTemplate<String, String> kafkaTemplate;


    @Autowired
    KafkaMessageSender(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;

    }

    void sendMessage(String message, String topicName) {
        kafkaTemplate.send(topicName, message);
    }
}
