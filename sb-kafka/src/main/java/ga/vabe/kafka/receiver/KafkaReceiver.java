package ga.vabe.kafka.receiver;

import com.fasterxml.jackson.databind.ObjectMapper;
import ga.vabe.kafka.domain.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Slf4j
public class KafkaReceiver {

    private ObjectMapper mapper = new ObjectMapper();

    private ExecutorService pool = Executors.newFixedThreadPool(10);

    @KafkaListener(topics = {"test-topic"})
    public void listen(String message) {
        pool.execute(() -> {
            try {
                log.info("------------------接收消息 message =" + message);
                System.out.println(Thread.currentThread().getName());
                Thread.sleep(1000000);
                Message msg = mapper.readValue(message, Message.class);
                log.info("MessageConsumer: onMessage: message is: [" + msg + "]");
                log.info("------------------ message =" + message);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });


    }

}