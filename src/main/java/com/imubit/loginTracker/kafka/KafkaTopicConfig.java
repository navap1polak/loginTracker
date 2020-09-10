package com.imubit.loginTracker.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
class KafkaTopicConfig {

    @Value("${kafka.input.topic}")
    private String kafkaInputTopic;

    @Value("${kafka.output.topic}")
    private String kafkaOutputTopic;

    @Bean
    public NewTopic inputTopic() {
        NewTopic newTopic = new NewTopic(kafkaInputTopic, 1, (short) 1);

        return newTopic;
    }

    @Bean
    public NewTopic outputTopic() {
        NewTopic newTopic = new NewTopic(kafkaOutputTopic, 1, (short) 1);

        return newTopic;
    }


}
