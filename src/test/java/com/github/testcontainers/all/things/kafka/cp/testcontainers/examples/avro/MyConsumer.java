package com.github.testcontainers.all.things.kafka.cp.testcontainers.examples.avro;

import java.util.Properties;

public interface MyConsumer {

  Properties createConsumerProperties(String bootstrapServer);

  void consume();

}
