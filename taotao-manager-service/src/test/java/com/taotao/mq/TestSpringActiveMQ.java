package com.taotao.mq;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.*;

public class TestSpringActiveMQ {

    public void testQueueProducer() {
        // 初始化spring容器
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-activemq.xml");

        // 从容器获得JMSTemlate对象
        JmsTemplate jmsTemplate = applicationContext.getBean(JmsTemplate.class);

        // 从容器中获得Destination对象
        Queue queue = applicationContext.getBean(Queue.class);

        jmsTemplate.send(queue, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                TextMessage textMessage = session.createTextMessage("使用spring和ActiveMQ整合发送queue消息");
                return textMessage;
            }
        });
    }
}
