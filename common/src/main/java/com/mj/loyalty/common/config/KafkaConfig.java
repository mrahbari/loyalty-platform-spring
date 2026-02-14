package com.mj.loyalty.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mj.loyalty.common.events.BaseEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.producer.acks:-1}")
    private String acks;

    @Value("${spring.kafka.producer.retries:3}")
    private int retries;

    @Value("${spring.kafka.producer.enable.idempotence:true}")
    private boolean enableIdempotence;

    @Bean
    public ProducerFactory<String, Object> producerFactory(ObjectMapper objectMapper) {
        Map<String, Object> props = new HashMap<>();
        props.put(org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.StringSerializer.class);
        props.put(org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        
        // Configure delivery semantics
        // For at-least-once delivery (default), we ensure acknowledgments and retries
        props.put(org.apache.kafka.clients.producer.ProducerConfig.ACKS_CONFIG, acks); // -1 (all replicas) for strong durability
        props.put(org.apache.kafka.clients.producer.ProducerConfig.RETRIES_CONFIG, retries);
        props.put(org.apache.kafka.clients.producer.ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, enableIdempotence); // Prevents duplicate messages
        
        return new DefaultKafkaProducerFactory<>(props, 
            new org.apache.kafka.common.serialization.StringSerializer(),
            new JsonSerializer<>(objectMapper));
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    // Define topics
    @Bean
    public NewTopic userRegisteredTopic() {
        return TopicBuilder.name("UserRegistered")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic voucherIssuedTopic() {
        return TopicBuilder.name("VoucherIssued")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic voucherRedeemedTopic() {
        return TopicBuilder.name("VoucherRedeemed")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic pointsEarnedTopic() {
        return TopicBuilder.name("PointsEarned")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic pointsRedeemedTopic() {
        return TopicBuilder.name("PointsRedeemed")
                .partitions(3)
                .replicas(1)
                .build();
    }
}