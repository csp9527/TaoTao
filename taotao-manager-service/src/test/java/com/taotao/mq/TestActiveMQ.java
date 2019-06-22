package com.taotao.mq;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.junit.Test;

import javax.jms.*;

public class TestActiveMQ {

    public void testQueueProducer() throws Exception {
        // 1. 创建一个连接工厂---ConnectionFactory
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.203.134:61616");

        // 2. 使用 ConnectionFactory 对象来创建一个Connection对象
        Connection connection = connectionFactory.createConnection();

        // 3. 开启连接
        connection.start();

        /*
         * 4.使用Connection对象创建一个Session对象
         *   第一个参数：是否开启事务(ActiceMQ的事务)，一般不使用分布式事务，因为它特别消耗性能，而且顾客体验特别差，
         *            现在互联网的做法是保证数据的最终一致(也就是允许暂时数据不一致)。比如顾客下单购买东西，一旦订单生成完就立刻响应给用户
         *            下单成功。至于下单后一系列的操作，比如通知会计记账、通知物流发货、商品数量同步等等都先不用管，只需要发送一条消息到消息队列，
         *            消息队列来告知各模块进行相应的操作，一次告知不行就两次，直到完成所有相关操作为止，这也就做到了数据的最终一致性。
         *            如果第一个参数设为true，那么第二个参数将会被直接忽略掉。如果第一个参数为false，那么第二个参数才有意义。
         *   第二个参数：消息的应答模式，常见的有手动应答和自动应答两种模式。我们一般使用自动应答模式。
         */

        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        // 5. 使用Session对象创建一个Destination对象， 目的地有两种形式， 一种是queue， 一种是topic
        Queue queue = session.createQueue("test-queue");

        // 6. 使用session对象创建一个producer对象
        MessageProducer producer = session.createProducer(queue);

        // 7. 使用producer对象发生消息
        TextMessage textMessage = new ActiveMQTextMessage();
        textMessage.setText("使用ActiveMQ发送队列消息");
        producer.send(textMessage);

        // 8. 关闭资源
        producer.close();
        session.close();
        connection.close();

    }

    public void testQueueConsumer() throws Exception {
        // 1. 创建一个连接工厂对象
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.203.134:61616");

        // 2. 创建连接
        Connection connection = connectionFactory.createConnection();

        // 开启连接
        connection.start();

        // 3. 使用连接对象创建一个session对象
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        // 4. 使用session对象创建一个destination对象， 使用queue
        Queue queue = session.createQueue("test-queue");

        // 5. 使用session对象创建一个消费者
        MessageConsumer consumer = session.createConsumer(queue);

        // 6. 使用消费者对象接收消息
        consumer.setMessageListener((Message message) -> {
            // 7. 打印消息
            TextMessage textMessage = (TextMessage) message;
            String text = "";
            try {
                text = textMessage.getText();
                System.out.println(text);
            } catch (JMSException e) {
                e.printStackTrace();
            }
        });

        System.in.read();

        // 8. 关闭资源
        consumer.close();
        session.close();
        connection.close();
    }
}
