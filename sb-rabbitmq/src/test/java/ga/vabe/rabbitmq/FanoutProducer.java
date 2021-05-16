package ga.vabe.rabbitmq;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

/**
 * 发布/订阅模式
 * 生产者客户端代码
 */
public class FanoutProducer {

    public static void main(String[] args) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(Constant.HOST);
        factory.setPort(5672);
        factory.setUsername(Constant.USERNAME);
        factory.setPassword(Constant.PASSWORD);
        // 创建连接 & 信道
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.exchangeDeclare(Constant.EXCHANGE_FANOUT, BuiltinExchangeType.FANOUT);
            String message = "RabbitMq fanout。。。。。。";
            channel.basicPublish(Constant.EXCHANGE_FANOUT, "", null, message.getBytes(StandardCharsets.UTF_8));
            System.out.println(" [x] Sent '" + message + "'");
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }

    }
}
