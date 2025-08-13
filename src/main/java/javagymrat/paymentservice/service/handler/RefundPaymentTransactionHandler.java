package javagymrat.paymentservice.service.handler;

import javagymrat.paymentservice.controller.kafka.producer.PaymentTransactionProducer;
import javagymrat.paymentservice.model.dto.CancelPaymentRequest;
import javagymrat.paymentservice.model.enums.PaymentTransactionCommand;
import javagymrat.paymentservice.service.validator.PaymentTransactionValidator;
import javagymrat.paymentservice.service.RefundService;
import javagymrat.paymentservice.util.JsonConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Обрабатывает команды на возврат платежа
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RefundPaymentTransactionHandler implements PaymentTransactionCommandHandler {
    private final JsonConverter jsonConverter;
    private final PaymentTransactionValidator paymentTransactionValidator;
    private final RefundService refundService;
    private final PaymentTransactionProducer paymentTransactionProducer;

    @Override
    public void processCommand(Long requestId, String message) {
        var request = jsonConverter.fromJson(message, CancelPaymentRequest.class);
        paymentTransactionValidator.validateCancelTransactionRequest(request);
        var result = refundService.cancelPayment(request);

        paymentTransactionProducer.sendCommandResult(requestId, PaymentTransactionCommand.REFUND, result.toString());
    }
}
