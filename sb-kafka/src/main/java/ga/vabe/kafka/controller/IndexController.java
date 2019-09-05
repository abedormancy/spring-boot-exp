package ga.vabe.kafka.controller;

import ga.vabe.kafka.domain.Message;
import ga.vabe.kafka.provider.KafkaSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@RestController
@RequestMapping("/")
@Slf4j
public class IndexController {

    @Autowired
    private KafkaSender sender;

    @RequestMapping(produces = MediaType.TEXT_PLAIN_VALUE)
    public String index() {
        return "hello world";
    }

    @GetMapping(value = "/kafka/send", produces = MediaType.TEXT_PLAIN_VALUE)
    public String sendKafka(WebRequest request) {
        String content = request + ": " + System.currentTimeMillis();
        Message message = new Message();
        message.setMsg(content);
        message.setSendTime(new Date());
        try {
            log.info("kafka的消息={}", message);
            sender.send(message);
            log.info("发送kafka成功.");
        } catch (Exception e) {
            log.error("发送kafka失败", e);
        }
        return content;
    }


}
