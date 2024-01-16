package com.irvan.ArtemisMassSender.config;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

@EnableJms
@Configuration
public class QueueConfig {
    private static final Logger log = LoggerFactory.getLogger(QueueConfig.class);

    @Value("#{${custom.app.name}}")
    private String appName;
    @Value("${app.artemis.queue.broker-url}")
    private String brokerUrl;
    @Value("${app.artemis.queue.user}")
    private String user;
    @Value("${app.artemis.queue.password}")
    private String password;
    @Value("#{${app.artemis.queue.connection-cache}}")
    private Integer connectionCache;
    @Value("${app.artemis.queue.concurrency}")
    private String concurrency;
    @Value("${app.artemis.queue.connection-transacted}")
    private String connectionTransacted;
    @Value("#{${app.artemis.queue.max-message-per-task}}")
    private Integer maxMessagePerTask;
    @Value("#{${app.artemis.queue.receive-timeout}}")
    private Long receiveTimeout;

    @Bean
    public ConnectionFactory connectionFactory() throws JMSException {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL(brokerUrl);
        connectionFactory.setUser(user);
        connectionFactory.setPassword(password);
        return connectionFactory;
    }

    @Bean
    public ConnectionFactory cachingConnectionFactory() throws JMSException {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setTargetConnectionFactory(connectionFactory());
        connectionFactory.setSessionCacheSize(connectionCache);
        connectionFactory.setCacheProducers(true);
        connectionFactory.setReconnectOnException(true);
        return connectionFactory;
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(
            DefaultJmsListenerContainerFactoryConfigurer configurer,
            ConnectionFactory connectionFactory) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        factory.setConcurrency(concurrency);
        factory.setSessionTransacted(Boolean.valueOf(connectionTransacted));
        factory.setMaxMessagesPerTask(maxMessagePerTask);
        factory.setReceiveTimeout(receiveTimeout);
        return factory;
    }

    @Bean
    public JmsTemplate jmsTemplate() throws JMSException {
        JmsTemplate template = new JmsTemplate();
        template.setExplicitQosEnabled(true);
        template.setConnectionFactory(cachingConnectionFactory());
        return template;
    }

    @EventListener
    public void onStartup(ApplicationReadyEvent event) {
        log.info("[" + appName + "] | " + getClass().getSimpleName().split("\\$")[0] + " is ready.");
    }
}
