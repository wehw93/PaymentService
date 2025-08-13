package javagymrat.paymentservice.config;

import javagymrat.paymentservice.model.enums.PaymentTransactionCommand;
import javagymrat.paymentservice.service.handler.CreatePaymentTransactionHandler;
import javagymrat.paymentservice.service.handler.PaymentTransactionCommandHandler;
import javagymrat.paymentservice.service.handler.RefundPaymentTransactionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class PaymentTransactionCommandConfig {

    @Bean
    public Map<PaymentTransactionCommand, PaymentTransactionCommandHandler> commandHandlers(
            CreatePaymentTransactionHandler createPaymentTransactionHandler,
            RefundPaymentTransactionHandler refundPaymentTransactionHandler
    ) {
        return Map.of(
                PaymentTransactionCommand.CREATE, createPaymentTransactionHandler,
                PaymentTransactionCommand.REFUND, refundPaymentTransactionHandler
        );
    }
}
