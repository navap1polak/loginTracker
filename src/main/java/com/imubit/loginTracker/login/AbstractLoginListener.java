package com.imubit.loginTracker.login;

import com.imubit.loginTracker.kafka.KafkaMessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * Abstract class for getting new logged users from operating system.
 * Need to extend for each operating system
 */
public abstract class AbstractLoginListener {
    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    @Value("${kafka.input.topic}")
    private String kafkaInputTopic;

    public abstract void init();

    protected void sendMessage(String message){
        kafkaMessageSender.sendMessage(kafkaInputTopic, message);
    }
}
