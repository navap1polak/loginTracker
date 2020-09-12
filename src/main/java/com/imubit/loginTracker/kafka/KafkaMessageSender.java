package com.imubit.loginTracker.kafka;

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

    public void sendMessage(String topicName,String message ) {
        kafkaTemplate.send(topicName, message);
    }
}
