package com.github.testcontainers.all.things.kafka.cp.testcontainers.examples.misc;

import com.github.dockerjava.api.command.InspectContainerResponse;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.Transferable;

import java.nio.charset.StandardCharsets;

/**
 * Redpanda Container
 * based on @bsideup's gist https://gist.github.com/bsideup/21762b4089a244e6fbfcfc606e7f0394
 */
public class RedpandaContainer extends GenericContainer<RedpandaContainer> {

  private static final String STARTER_SCRIPT = "/testcontainers_start.sh";
  public static final int KAFKA_PORT = 9092;

  public RedpandaContainer() {
    super("vectorized/redpanda:v21.4.12");

    withExposedPorts(KAFKA_PORT);
    withCreateContainerCmdModifier(cmd -> {
      cmd.withEntrypoint("sh");
    });

    withCommand("-c", "while [ ! -f " + STARTER_SCRIPT + " ]; do sleep 0.1; done; " + STARTER_SCRIPT);
    waitingFor(Wait.forLogMessage(".*Started Kafka API server.*", 1));
  }

  public String getBootstrapServers() {
    return String.format("PLAINTEXT://%s:%s", getHost(), getMappedPort(KAFKA_PORT));
  }

  @Override
  protected void containerIsStarting(InspectContainerResponse containerInfo) {
    super.containerIsStarting(containerInfo);

    String command = "#!/bin/bash\n";

    command += "/usr/bin/rpk redpanda start --check=false --node-id 0 ";
    command += "--kafka-addr PLAINTEXT://0.0.0.0:29092,OUTSIDE://0.0.0.0:9092 ";
    command += "--advertise-kafka-addr PLAINTEXT://kafka:29092,OUTSIDE://" + getHost() + ":" + getMappedPort(KAFKA_PORT);

    copyFileToContainer(
        Transferable.of(command.getBytes(StandardCharsets.UTF_8), 0777),
        STARTER_SCRIPT
    );
  }
}