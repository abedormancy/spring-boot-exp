package ga.vabe.kafka.provider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Component
@Slf4j
public class KafkaSender<T> {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    private ObjectMapper mapper = new ObjectMapper();

    //发送消息方法
    public void send(T obj) throws JsonProcessingException {
//        String jsonObj = JSON.toJSONString(obj);
        String jsonObj = mapper.writeValueAsString(obj);
        log.info("------------ message = {}", jsonObj);
        //发送消息
        ListenableFuture<SendResult<String, Object>> future = kafkaTemplate.send("test-topic", jsonObj);
        future.addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {
            @Override
            public void onFailure(Throwable throwable) {
                log.info("Produce: The message failed to be sent:" + throwable.getMessage());
            }

            @Override
            public void onSuccess(SendResult<String, Object> stringObjectSendResult) {
                //TODO 业务处理
                log.info("Produce: The message was sent successfully:");
                log.info("Produce: _+_+_+_+_+_+_+ result: " + stringObjectSendResult.toString());
            }
        });
    }
}