package ga.vabe.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

/**
 * 生产者客户端代码
 */
public class DirectProducer {

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(Constant.HOST);
        factory.setPort(5672);
        factory.setUsername(Constant.USERNAME);
        factory.setPassword(Constant.PASSWORD);
        // 创建连接
        final Connection connection = factory.newConnection();
        // 创建信道
        final Channel channel = connection.createChannel();
        // 创建一个 type = "direct“ ，持久化、非自动删除的交换器
        channel.exchangeDeclare(Constant.EXCHANGE, "direct", true, false, null);
        // 创建一个持久化、非排他的、非自动删除的队列
        channel.queueDeclare(Constant.QUEUE, true, false, false, null);
        // 将交换机与队列通过路由键绑定
        channel.queueBind(Constant.QUEUE, Constant.EXCHANGE, Constant.ROUTING_KEY);

        String message = "hello world, 你是猪呀";

        channel.basicPublish(Constant.EXCHANGE, Constant.ROUTING_KEY,
                MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes(StandardCharsets.ISO_8859_1));
        channel.close();
        connection.close();
    }
}
