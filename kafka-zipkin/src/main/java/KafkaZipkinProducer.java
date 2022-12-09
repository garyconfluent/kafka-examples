
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import brave.kafka.interceptor.TracingProducerInterceptor;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class KafkaZipkinProducer {
    private static CountDownLatch latch = new CountDownLatch(1);

    public static void main(String[] args) throws InterruptedException {

        Producer<String, String> producer = new KafkaProducer<>(newProducerConfig());

        // Add shutdown hook to respond to SIGTERM and gracefully stop the application.q
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Closing application gracefully (SIGTERM)");
            latch.countDown();
            producer.close();
            System.out.println("Closed");
        }));

        while (latch.getCount() > 0) {
            producer.send(new ProducerRecord<>("my-topic", "my-key", "my-value"), (metadata, exception) -> {
                if (exception != null) {
                    exception.printStackTrace();
                } else {

                    System.out.printf("Successfully send record to topic=%s, partition=%s with offset=%d\n",
                            metadata.topic(), metadata.partition(), metadata.offset());
                }
            });
            if (latch.await(1, TimeUnit.SECONDS)) {
                break;
            }
        }
        System.out.println("Stop to produce records");
    }

    private static Properties newProducerConfig() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:19092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.ACKS_CONFIG, "1");

        // Configure interceptor and attached configuration.
        props.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, TracingProducerInterceptor.class.getName());
        props.setProperty("zipkin.http.endpoint", "http://127.0.0.1:9411/api/v2/spans");
        props.setProperty("zipkin.sender.type", "HTTP");
        props.setProperty("zipkin.encoding", "JSON");
        props.setProperty("zipkin.remote.service.name", "kafka");
        props.setProperty("zipkin.local.service.name", "producer");
        props.setProperty("zipkin.trace.id.128bit.enabled", "true");
        props.setProperty("zipkin.sampler.rate", "1.0F");
        return props;
    }
}
