package ga.vabe.rabbitmq;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 消费者客户端代码，推模式
 */
public class ConsumerTest01 {

    public static void main(String[] args) throws IOException, TimeoutException {
        Address[] addresses = new Address[]{new Address(Constant.HOST, Constant.PORT)};
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername(Constant.USERNAME);
        factory.setPassword(Constant.PASSWORD);
        // 创建连接
        final Connection connection = factory.newConnection(addresses);
        // 创建信道
        final Channel channel = connection.createChannel();
        // 客户端最多接受未被ack的消息数量
        channel.basicQos(64);
        final Consumer consumer = new DefaultConsumer(channel) {

            @Override
            public void handleDelivery(String consumerTag,
                                       Envelope envelope,
                                       AMQP.BasicProperties properties,
                                       byte[] body)
                    throws IOException {
                System.out.println("recv message: " + new String(body, StandardCharsets.UTF_8));
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        };
        channel.basicConsume(Constant.QUEUE, consumer);
    }
}
