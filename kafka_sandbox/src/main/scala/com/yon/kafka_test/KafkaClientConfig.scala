package com.yon.kafka_test

import org.apache.kafka.clients.producer.ProducerConfig.{BOOTSTRAP_SERVERS_CONFIG, CLIENT_ID_CONFIG}
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.scala.serialization.Serdes

object KafkaClientConfig {

  def consumerProps(clientId: Option[String], groupId: String, endpoint: String = "localhost:9092"): Map[String, Object] = {
    val props: Map[String, Object] = Map(
      "group.id" -> groupId,
      BOOTSTRAP_SERVERS_CONFIG -> endpoint,
      StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG -> Serdes.stringSerde.getClass
      // in case if needed custom serializer, e.g for protobuf
      //KEY_SERIALIZER_CLASS_CONFIG -> classOf[KafkaAvroSerializer],
      //VALUE_SERIALIZER_CLASS_CONFIG -> classOf[KafkaAvroSerializer],
      //SCHEMA_REGISTRY_URL_CONFIG -> "http://schema-registry:8081"
    ) ++ clientId.fold(Map[String, Object]())(id => Map(CLIENT_ID_CONFIG -> id))
    props
  }

  def producerProps(clientId: String, endpoint: String = "localhost:9092"): Map[String, Object] = {
    Map(
      clientId -> clientId,
      BOOTSTRAP_SERVERS_CONFIG -> endpoint,
      StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG -> Serdes.stringSerde.getClass
      // in case if needed custom serializer, e.g for protobuf
      //KEY_SERIALIZER_CLASS_CONFIG -> classOf[KafkaAvroSerializer],
      //VALUE_SERIALIZER_CLASS_CONFIG -> classOf[KafkaAvroSerializer],
      //SCHEMA_REGISTRY_URL_CONFIG -> "http://schema-registry:8081"
    )
  }

}
