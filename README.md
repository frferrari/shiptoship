# Ship To Ship Challenge

We are challenged to analyse a dataset containing ship movements and to identify STS (or ship to ship) transfers.

## Starting zookeeper and kafka

You could start zookeeper and kafka in 2 different terminal so that you can look at the log messages and check if anything fails.
The below commands have to be started from the kafka directory as the paths to the config files are relative.

```
zookeeper-server-start.sh config/zookeeper.properties

kafka-server-start.sh config/server.properties
```

## Creating the kafka topics

```
kafka-topics.sh --zookeeper 127.0.0.1:2181 --topic ais --create --partitions 4 --replication-factor 1

kafka-topics.sh --zookeeper 127.0.0.1:2181 --topic sts --create --partitions 1 --replication-factor 1
```

## Injecting the dataset

The command below allows to inject the full dataset in the input topic with a key being the harbor id (Galveston ...)

```
tail +2 data/Galveston_2017_11_25-26.csv | awk '{ printf("1234:%s\n", $0) }' | kafka-console-producer.sh \
--broker-list localhost:9092 \
--topic ais \
--property "parse.key=true" \
--property "key.separator=	"
```

## Output produced

Below is an extract of the data produced by the aisEvent consumer, the format is :

a key containing the harbor id and the 2 events related to an STS
an StsEvent containing 2 Sts objects identifying the STS

