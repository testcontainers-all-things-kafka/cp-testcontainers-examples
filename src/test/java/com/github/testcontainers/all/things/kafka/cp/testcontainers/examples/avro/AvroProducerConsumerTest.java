package com.github.testcontainers.all.things.kafka.cp.testcontainers.examples.avro;

import com.github.testcontainers.Movie;

import net.christophschubert.cp.testcontainers.CPTestContainerFactory;
import net.christophschubert.cp.testcontainers.SchemaRegistryContainer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.KafkaContainer;

import java.util.Collection;

import lombok.extern.slf4j.Slf4j;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class AvroProducerConsumerTest {

  static final CPTestContainerFactory testContainerFactory = new CPTestContainerFactory();

  final static KafkaContainer kafka = testContainerFactory.createKafka();
  final static SchemaRegistryContainer schemaRegistry = testContainerFactory.createSchemaRegistry(kafka);
  
  @BeforeAll
  public static void prep() {
    //kafka.start();
    schemaRegistry.start();
  }

  @Test
  public void testProducerConsumer() {
    TestAvroProducer helloProducer = new TestAvroProducer(schemaRegistry.getBaseUrl());
    helloProducer.createProducer(kafka.getBootstrapServers());

    TestAvroConsumer
        helloConsumer =
        new TestAvroConsumer(kafka.getBootstrapServers(), schemaRegistry.getBaseUrl());
    helloConsumer.consume();

    Collection<ConsumerRecord<String, Movie>> messages = helloConsumer.getReceivedRecords();

    assertThat(messages.size()).isEqualTo(5);
    messages.forEach(stringStringConsumerRecord -> {
      assertThat(stringStringConsumerRecord.key()).isEqualTo("Lethal Weapon");
      assertThat(stringStringConsumerRecord.value().getTitle()).isEqualTo("Lethal Weapon");
    });
  }

}
