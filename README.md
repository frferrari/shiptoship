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
kafka-topics.sh --zookeeper 127.0.0.1:2181 --topic ais --create --partitions 1 --replication-factor 1
kafka-topics.sh --zookeeper 127.0.0.1:2181 --topic sts --create --partitions 1 --replication-factor 1
```

## Injecting the dataset

The command below allows to inject the full dataset in the input topic with a key being the harbor id (Galveston ...)

```
tail +2 data/Galveston_2017_11_25-26.csv | awk '{ printf("120045806421:%s\n", $0) }' | kafka-console-producer.sh \
--broker-list localhost:9092 \
--topic ais \
--property "parse.key=true" \
--property "key.separator=:" \
```

## Output produced

Below is an extract of the data produced by the stsConsumer, the format is :

* a key containing the harbor id and the 2 event ids related to an STS
* an StsEvent containing 2 Sts objects identifying the STS

```
k=StsEventKey(120045806421,232621445,232622287) v=StsEvent(<<vessel=6653 sp=0.8 hd=22 co=5.0 eid=232621445 ets=2017-11-26 06:30:12.0>>,<<vessel=7642 sp=0.7 hd=20 co=3.0 eid=232622287 ets=2017-11-26 06:30:37.0>>)
k=StsEventKey(120045806421,232621905,232622010) v=StsEvent(<<vessel=4316 sp=0.1 hd=137 co=239.0 eid=232621905 ets=2017-11-26 06:29:07.0>>,<<vessel=5399 sp=0.3 hd=142 co=252.0 eid=232622010 ets=2017-11-26 06:25:52.0>>)
k=StsEventKey(120045806421,232629248,232630114) v=StsEvent(<<vessel=6653 sp=0.1 hd=18 co=249.0 eid=232629248 ets=2017-11-26 07:00:01.0>>,<<vessel=7642 sp=0.2 hd=16 co=267.0 eid=232630114 ets=2017-11-26 06:59:58.0>>)
k=StsEventKey(120045806421,232649602,232649435) v=StsEvent(<<vessel=4721 sp=3.6 hd=274 co=265.0 eid=232649602 ets=2017-11-26 08:30:40.0>>,<<vessel=5030 sp=3.6 hd=271 co=265.0 eid=232649435 ets=2017-11-26 08:29:53.0>>)
k=StsEventKey(120045806421,232693001,232693822) v=StsEvent(<<vessel=6653 sp=0.0 hd=65 co=313.0 eid=232693001 ets=2017-11-26 11:30:22.0>>,<<vessel=7642 sp=0.0 hd=64 co=187.0 eid=232693822 ets=2017-11-26 11:29:59.0>>)
k=StsEventKey(120045806421,232699020,232699779) v=StsEvent(<<vessel=6653 sp=0.0 hd=65 co=227.0 eid=232699020 ets=2017-11-26 11:50:12.0>>,<<vessel=7642 sp=0.1 hd=64 co=331.0 eid=232699779 ets=2017-11-26 11:57:00.0>>)
k=StsEventKey(120045806421,232705552,232706351) v=StsEvent(<<vessel=6653 sp=0.0 hd=68 co=188.0 eid=232705552 ets=2017-11-26 12:20:52.0>>,<<vessel=7642 sp=0.0 hd=66 co=250.0 eid=232706351 ets=2017-11-26 12:14:58.0>>)
k=StsEventKey(120045806421,232711980,232712809) v=StsEvent(<<vessel=6653 sp=0.0 hd=60 co=199.0 eid=232711980 ets=2017-11-26 12:59:20.0>>,<<vessel=7642 sp=0.1 hd=59 co=190.0 eid=232712809 ets=2017-11-26 12:56:59.0>>)
k=StsEventKey(120045806421,232717579,232718382) v=StsEvent(<<vessel=6653 sp=0.1 hd=56 co=201.0 eid=232717579 ets=2017-11-26 13:25:33.0>>,<<vessel=7642 sp=0.0 hd=55 co=161.0 eid=232718382 ets=2017-11-26 13:23:58.0>>)
k=StsEventKey(120045806421,232724936,232725739) v=StsEvent(<<vessel=6653 sp=0.1 hd=47 co=200.0 eid=232724936 ets=2017-11-26 13:55:40.0>>,<<vessel=7642 sp=0.1 hd=48 co=317.0 eid=232725739 ets=2017-11-26 13:47:59.0>>)
k=StsEventKey(120045806421,232730631,232731435) v=StsEvent(<<vessel=6653 sp=0.0 hd=48 co=170.0 eid=232730631 ets=2017-11-26 14:25:51.0>>,<<vessel=7642 sp=0.1 hd=45 co=209.0 eid=232731435 ets=2017-11-26 14:20:59.0>>)
k=StsEventKey(120045806421,232737072,232737876) v=StsEvent(<<vessel=6653 sp=0.0 hd=47 co=137.0 eid=232737072 ets=2017-11-26 14:55:11.0>>,<<vessel=7642 sp=0.1 hd=42 co=159.0 eid=232737876 ets=2017-11-26 14:47:59.0>>)
k=StsEventKey(120045806421,232750638,232751406) v=StsEvent(<<vessel=6653 sp=0.1 hd=47 co=173.0 eid=232750638 ets=2017-11-26 15:56:12.0>>,<<vessel=7642 sp=0.1 hd=45 co=172.0 eid=232751406 ets=2017-11-26 15:53:58.0>>)
k=StsEventKey(120045806421,232759439,232760224) v=StsEvent(<<vessel=6653 sp=0.1 hd=46 co=142.0 eid=232759439 ets=2017-11-26 16:25:31.0>>,<<vessel=7642 sp=0.0 hd=43 co=130.0 eid=232760224 ets=2017-11-26 16:23:59.0>>)
k=StsEventKey(120045806421,232765327,232766129) v=StsEvent(<<vessel=6653 sp=0.1 hd=50 co=145.0 eid=232765327 ets=2017-11-26 16:54:50.0>>,<<vessel=7642 sp=0.0 hd=48 co=323.0 eid=232766129 ets=2017-11-26 16:53:59.0>>)
k=StsEventKey(120045806421,232771969,232772760) v=StsEvent(<<vessel=6653 sp=0.1 hd=56 co=129.0 eid=232771969 ets=2017-11-26 17:25:32.0>>,<<vessel=7642 sp=0.1 hd=54 co=28.0 eid=232772760 ets=2017-11-26 17:24:00.0>>)
k=StsEventKey(120045806421,232777461,232778258) v=StsEvent(<<vessel=6653 sp=0.0 hd=47 co=158.0 eid=232777461 ets=2017-11-26 18:01:12.0>>,<<vessel=7642 sp=0.1 hd=47 co=151.0 eid=232778258 ets=2017-11-26 17:59:59.0>>)
k=StsEventKey(120045806421,232784297,232785076) v=StsEvent(<<vessel=6653 sp=0.1 hd=49 co=177.0 eid=232784297 ets=2017-11-26 18:25:21.0>>,<<vessel=7642 sp=0.0 hd=47 co=171.0 eid=232785076 ets=2017-11-26 18:23:59.0>>)
k=StsEventKey(120045806421,232790032,232790838) v=StsEvent(<<vessel=6653 sp=0.0 hd=50 co=160.0 eid=232790032 ets=2017-11-26 18:56:02.0>>,<<vessel=7642 sp=0.0 hd=48 co=138.0 eid=232790838 ets=2017-11-26 18:54:00.0>>)
k=StsEventKey(120045806421,232794437,232795255) v=StsEvent(<<vessel=6653 sp=0.1 hd=53 co=142.0 eid=232794437 ets=2017-11-26 19:26:21.0>>,<<vessel=7642 sp=0.1 hd=53 co=143.0 eid=232795255 ets=2017-11-26 19:23:59.0>>)
k=StsEventKey(120045806421,232802403,232803196) v=StsEvent(<<vessel=6653 sp=0.1 hd=51 co=168.0 eid=232802403 ets=2017-11-26 19:56:21.0>>,<<vessel=7642 sp=0.1 hd=51 co=163.0 eid=232803196 ets=2017-11-26 19:50:58.0>>)
k=StsEventKey(120045806421,232811012,232811819) v=StsEvent(<<vessel=6653 sp=0.1 hd=52 co=6.0 eid=232811012 ets=2017-11-26 20:23:52.0>>,<<vessel=7642 sp=0.1 hd=50 co=37.0 eid=232811819 ets=2017-11-26 20:14:59.0>>)
k=StsEventKey(120045806421,232816137,232816901) v=StsEvent(<<vessel=6653 sp=0.1 hd=51 co=155.0 eid=232816137 ets=2017-11-26 20:55:51.0>>,<<vessel=7642 sp=0.0 hd=48 co=27.0 eid=232816901 ets=2017-11-26 20:53:59.0>>)
k=StsEventKey(120045806421,232822547,232823356) v=StsEvent(<<vessel=6653 sp=0.0 hd=46 co=148.0 eid=232822547 ets=2017-11-26 21:26:21.0>>,<<vessel=7642 sp=0.0 hd=44 co=165.0 eid=232823356 ets=2017-11-26 21:23:59.0>>)
```
