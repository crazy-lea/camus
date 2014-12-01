#!/bin/sh

java -server -Djava.ext.dirs=camus-config/target/lib/ com.linkedin.camus.config.ConfigMain datacenter
hadoop jar camus-etl-kafka/target/camus-etl-kafka-0.1.0-SNAPSHOT-shaded.jar com.linkedin.camus.etl.kafka.CamusJob -P /etc/camusconfig/datacenter.camus.properties