package ga.vabe.kafka.receiver;

import com.fasterxml.jackson.databind.ObjectMapper;
import ga.vabe.kafka.domain.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Slf4j
public class KafkaReceiver {

    private ObjectMapper mapper = new ObjectMapper();

    private ExecutorService pool = Executors.newFixedThreadPool(10);

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    @KafkaListener(topics = {"test-topic"})
    public void consumer1(String message) {
        System.out.println("consumer1:");
        offer(message);
    }

    /**
     * 同一 topic 不同的 groupId 实现了多播的功能 (同一 groupId 下只会被一个消费者消费)
     * @param message
     */
    @KafkaListener(topics = {"test-topic"}, groupId = "dudulu2")
    public void consumer2(String message) {
        System.out.println("consumer2:");
        offer(message);
    }

    public void offer(String message) {
        pool.execute(() -> {
            try {
                String thread = Thread.currentThread().getName();
                StringBuilder sb = new StringBuilder(message.length() << 1);
                sb.append(LINE_SEPARATOR).append("----").append(thread).append("---------接收消息 message =").append(message).append(LINE_SEPARATOR);
                Message msg = mapper.readValue(message, Message.class);
                sb.append("MessageConsumer: onMessage: message is: [").append(msg).append("]").append(LINE_SEPARATOR);
                sb.append("-------------------------------- message over-------").append(LINE_SEPARATOR);
                log.info(sb.toString());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }


}