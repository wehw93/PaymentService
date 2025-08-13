package javagymrat.paymentservice.service.validator;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import javagymrat.paymentservice.model.dto.CancelPaymentRequest;
import javagymrat.paymentservice.model.dto.CreatePaymentTransactionRequest;
import javagymrat.paymentservice.model.entity.PaymentTransaction;
import javagymrat.paymentservice.model.enums.PaymentTransactionStatus;
import javagymrat.paymentservice.model.exception.PaymentTransactionValidationException;
import javagymrat.paymentservice.service.BankAccountService;
import javagymrat.paymentservice.service.PaymentTransactionService;
import javagymrat.paymentservice.service.RefundService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentTransactionValidator {
    private final BankAccountService bankAccountService;
    private final PaymentTransactionService paymentTransactionService;
    private final RefundService refundService;
    private final Validator validator;

    public void validateCreateTransactionRequest(CreatePaymentTransactionRequest transaction) {
        List<String> errors = new ArrayList<>();

        var violations = validator.validate(transaction, CreatePaymentTransactionRequest.class);
        var violationMessages = violations.stream().map(ConstraintViolation::getMessage).toList();
        errors.addAll(violationMessages);

        // Проверка наличия sourceAccountId
        if (bankAccountService.findOptionalById(transaction.getSourceAccountId()).isEmpty()) {
            errors.add("Source bank account does not exist: " + transaction.getSourceAccountId());
        }

        if (transaction.getDestinationAccountId() != null &&
                bankAccountService.findOptionalById(transaction.getDestinationAccountId()).isEmpty()) {
            errors.add("Destination bank account does not exist: " + transaction.getDestinationAccountId());
        }

        if(!errors.isEmpty()) {
            throw new PaymentTransactionValidationException(errors);
        }
    }

    public void validateCancelTransactionRequest(CancelPaymentRequest cancelPaymentRequest){
        List<String> errors = new ArrayList<>();

        var violations = validator.validate(cancelPaymentRequest, CancelPaymentRequest.class);
        var violationMessages = violations.stream().map(ConstraintViolation::getMessage).toList();
        errors.addAll(violationMessages);

        // Проверяем, существует ли транзакция
        PaymentTransaction sourceTransaction = paymentTransactionService.findOptionalById(cancelPaymentRequest.getTransactionId())
                .orElse(null);
        if (sourceTransaction == null) {
            errors.add("Transaction with ID " + cancelPaymentRequest.getTransactionId() + " does not exist.");
        } else {
            // Проверяем, завершена ли транзакция (FAILED или SUCCESS)
            if (!sourceTransaction.getStatus().equals(PaymentTransactionStatus.SUCCESS)) {
                errors.add("Transaction is not completed successfully, cancellation is not allowed.");
            }

            // Вычисляем уже возвращенную сумму.
            var alreadyRefunded = refundService.getTotalRefundedAmount(sourceTransaction.getId());
            var remainingAmount = cancelPaymentRequest.getRefundedAmount().subtract(alreadyRefunded);

            if (cancelPaymentRequest.getRefundedAmount().compareTo(remainingAmount) > 0) {
                errors.add("Cancel amount exceeds available refundable balance. Available: " + remainingAmount);
            }
        }

        // Если есть ошибки, выбрасываем исключение
        if (!errors.isEmpty()) {
            throw new PaymentTransactionValidationException(errors);
        }
    }

}
