package com.irvan.ArtemisMassSender.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class QueueProducer {
    private static final Logger log = LoggerFactory.getLogger(QueueProducer.class);
    @Value("#{${custom.app.name}}")
    private String appName;
    private final JmsTemplate jmsTemplate;

    @Autowired
    public QueueProducer(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void sendMessage(String queueName, String message, Integer priority) {
        int i = 0;
        log.info("[QueueProducer][sendQueue] Sending message to " + queueName + " : " + message );
        try {
            jmsTemplate.setPriority(priority);
            jmsTemplate.convertAndSend(queueName, message);
            log.info("[QueueProducer][sendQueue] Success");

        } catch (Exception ex) {
            log.error("[QueueProducer][sendQueue] Error sending message to queue caused by : ", ex);
        }
    }

    @EventListener
    public void onStartup(ApplicationReadyEvent event) {
        log.info("[" + appName + "] | " + getClass().getSimpleName() + " is ready.");
    }
}
