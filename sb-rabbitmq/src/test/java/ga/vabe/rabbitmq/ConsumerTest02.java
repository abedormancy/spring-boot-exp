package ga.vabe.rabbitmq;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

/**
 * 消费者客户端代码，拉模式
 */
public class ConsumerTest02 {

    public static void main(String[] args) throws IOException, TimeoutException {
        Address[] addresses = new Address[]{new Address(Constant.HOST, Constant.PORT)};
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername(Constant.USERNAME);
        factory.setPassword(Constant.PASSWORD);

        try (final Connection connection = factory.newConnection(addresses);
             Channel channel = connection.createChannel()) {
            GetResponse response = channel.basicGet(Constant.QUEUE, false);
            if (response != null) {
                System.out.println("message count: " + response.getMessageCount());
                System.out.println("recv message: " + new String(response.getBody(), StandardCharsets.UTF_8));
                channel.basicAck(response.getEnvelope().getDeliveryTag(), false);
            }
        }

    }
}
