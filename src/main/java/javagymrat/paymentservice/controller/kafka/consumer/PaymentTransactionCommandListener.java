package javagymrat.paymentservice.controller.kafka.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import javagymrat.paymentservice.model.enums.PaymentTransactionCommand;
import javagymrat.paymentservice.service.handler.PaymentTransactionCommandHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Слушает команды на работу с платежными транзакциями
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentTransactionCommandListener {
    private final Map<PaymentTransactionCommand, PaymentTransactionCommandHandler> commandHandlers;

    @KafkaListener(topics = "payment-command", containerFactory = "kafkaListenerContainerFactory")
    public void consumeCommand(ConsumerRecord<String, String> record) throws JsonProcessingException {
        log.info("Payment command received, command:{}", record);

        if (extractCommand(record).equals(PaymentTransactionCommand.UNKNOWN)) {
            throw new IllegalArgumentException("Unknown command");
        }

        commandHandlers.get(extractCommand(record)).processCommand(Long.valueOf(record.key()), record.value());
    }

    private PaymentTransactionCommand extractCommand(ConsumerRecord<String, String> record) {
        var header = record.headers().lastHeader("command");
        if (header != null) {
            return PaymentTransactionCommand.fromString(new String(header.value(), StandardCharsets.UTF_8));
        }
        return PaymentTransactionCommand.UNKNOWN;
    }
}
