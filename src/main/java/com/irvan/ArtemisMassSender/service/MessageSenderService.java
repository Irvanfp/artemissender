package com.irvan.ArtemisMassSender.service;

import com.google.gson.Gson;
import com.irvan.ArtemisMassSender.config.QueueProducer;
import com.irvan.ArtemisMassSender.model.Watosmscontroller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@EnableAsync
public class MessageSenderService {
    private static final Logger log = LoggerFactory.getLogger(MessageSenderService.class);

    @Value("#{${custom.app.name}}")
    private String appName;

    @Value("${app.artemis.queue.target}")
    private String queueName;

    @Value("${config.artemis.queuePriority}")
    private String queuePriority;
    @Autowired
    private QueueProducer queueProducer;

    private final Gson gson = new Gson();

    @PostMapping("/{queue}/{value}")
    public HttpStatus sendMessage(@PathVariable int value, @PathVariable String queue, @RequestBody String message) {
        try {
            UUID uid = null;
            int number = 0;
            for (int i = 0; i < value; i++) {
                uid = UUID.randomUUID();
                String trxid = uid.toString();
                String bogusNum = String.valueOf(i);
                String bogusNumFinal = bogusNum+bogusNum+bogusNum;
                number++;
                if (message.contains("{sleep}")&&(i%2==0)) {
                    String msg = message.replace("{sleep}", "sleepEven.php");
                    String num = msg.replace("6281802070002", "6281802070"+bogusNumFinal);
                    log.info("[MessageSenderService][sendMessage] Sending " + number + "message");
                    queueProducer.sendMessage(queue, num, Integer.valueOf(queuePriority));
                } else if(message.contains("{sleep}")&&(i%2!=0)){
                    String msg = message.replace("{sleep}", "sleepOdd.php");
                    String num = msg.replace("6281802070002", "6281802070"+bogusNumFinal);
                    log.info("[MessageSenderService][sendMessage] Sending " + number + "message");
                    queueProducer.sendMessage(queue, num, Integer.valueOf(queuePriority));
                }
                else {
                    log.info("[MessageSenderService][sendMessage] Sending " + number + "message");
                    queueProducer.sendMessage(queue, message, Integer.valueOf(queuePriority));
                }
            }
            log.info("[MessageSenderService][sendMessage] Success sent " + number + " message(s)");
            return HttpStatus.OK;
        } catch (Exception e) {
            log.error("[MessageSenderService][sendMessage] There has been an error on : " + e.toString());
            return HttpStatus.NOT_ACCEPTABLE;
        }
    }

    @EventListener
    public void onStartup(ApplicationReadyEvent event) {
        log.info("[" + appName + "] | " + getClass().getSimpleName().split("\\$")[0] + " is ready.");
    }
}
