package com.irvan.ArtemisMassSender.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

@Service
@EnableAsync
public class MessageListener {
    private static final Logger log = LoggerFactory.getLogger(MessageListener.class);
    @Value("#{${custom.app.name}}")
    private String appName;
    @JmsListener(destination = "${app.artemis.queue.listen}")
    public void receive(String message) throws InterruptedException {
        log.info(message);
    }
    @EventListener
    public void onStartup(ApplicationReadyEvent event) {
        log.info("[" + appName + "] | " + getClass().getSimpleName().split("\\$")[0] + " is ready.");
    }
}
