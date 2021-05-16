package ga.vabe.rabbitmq;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class FanoutConsumer {

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(Constant.HOST);
        factory.setPort(Constant.PORT);
        factory.setUsername(Constant.USERNAME);
        factory.setPassword(Constant.PASSWORD);

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(Constant.EXCHANGE_FANOUT, BuiltinExchangeType.FANOUT);
        // 生成一个非持久化、专有的、自动删除的、名字随机生成的队列名称
        final String queue = channel.queueDeclare().getQueue();
        channel.queueBind(queue, Constant.EXCHANGE_FANOUT, "");
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println(" [x] Received '" + message + "'");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        // 因为队列是随机的, autoAck 理应设置成 true
        channel.basicConsume(queue, true, deliverCallback, consumerTag -> {});


    }
}
