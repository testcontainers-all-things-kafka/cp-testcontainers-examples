package com.github.testcontainers.all.things.kafka.cp.testcontainers.examples.misc;

import net.christophschubert.cp.testcontainers.util.TestClients;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.output.Slf4jLogConsumer;

import java.util.Collections;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class RedpandaContainerTest {

  static RedpandaContainer rpk = new RedpandaContainer().withLogConsumer(new Slf4jLogConsumer(log));


  @BeforeAll
  static void startRp() {
    rpk.start();
  }

  @Test
  public void shouldProduceAndConsumeMessageFromRedpanda() {
    final Producer<String, String> producer = TestClients.createProducer(rpk.getBootstrapServers());
    producer.send(new ProducerRecord<>("rpk", "hello", "world"),
                  (metadata, exception) -> log.info("Producer got offset {} in topic {} on timestamp {}", metadata.offset(), metadata.topic(), metadata.timestamp()));

    final TestClients.TestConsumer<String, String> consumer = TestClients.createConsumer(rpk.getBootstrapServers());
    consumer.subscribe(Collections.singleton("rpk"));
    final List<String> strings = consumer.consumeUntil(1);
    assertThat(strings.size()).isEqualTo(1);

  }

}