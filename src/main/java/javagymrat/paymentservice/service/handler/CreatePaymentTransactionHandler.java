package javagymrat.paymentservice.service.handler;

import javagymrat.paymentservice.controller.kafka.producer.PaymentTransactionProducer;
import javagymrat.paymentservice.mapper.PaymentTransactionMapper;
import javagymrat.paymentservice.model.dto.CreatePaymentTransactionRequest;
import javagymrat.paymentservice.model.entity.BankAccount;
import javagymrat.paymentservice.model.enums.PaymentTransactionCommand;
import javagymrat.paymentservice.model.enums.PaymentTransactionStatus;
import javagymrat.paymentservice.service.BankAccountService;
import javagymrat.paymentservice.service.PaymentTransactionService;
import javagymrat.paymentservice.service.validator.PaymentTransactionValidator;
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
public class CreatePaymentTransactionHandler implements PaymentTransactionCommandHandler{
    private final PaymentTransactionService paymentTransactionService;
    private final PaymentTransactionMapper paymentTransactionMapper;
    private final JsonConverter jsonConverter;
    private final PaymentTransactionValidator paymentTransactionValidator;
    private final PaymentTransactionProducer paymentTransactionProducer;
    private final BankAccountService bankAccountService;

    @Override
    public void processCommand(Long requestId, String message) {
        var request = jsonConverter.fromJson(message, CreatePaymentTransactionRequest.class);
        paymentTransactionValidator.validateCreateTransactionRequest(request);

        var sourceAccount = bankAccountService.findOptionalById(request.getSourceAccountId()).get();
        BankAccount destinationBankAccount = null;
        if(request.getDestinationAccountId() != null){
            destinationBankAccount = bankAccountService.findOptionalById(request.getDestinationAccountId()).get();
        }

        var result = paymentTransactionService.save(
                paymentTransactionMapper.toEntity(request, sourceAccount, destinationBankAccount, PaymentTransactionStatus.SUCCESS)
        );
        paymentTransactionProducer.sendCommandResult(requestId, PaymentTransactionCommand.CREATE, result.toString());
    }
}
