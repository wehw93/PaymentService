package javagymrat.paymentservice.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CancelPaymentRequest {
    @NotNull(message = "Transaction ID must not be null")
    private Long transactionId;
    @NotNull(message = "Cancel amount must not be null")
    @Min(value = 1, message = "Cancel amount must be greater than zero")
    private BigDecimal refundedAmount;
    private String reason;
}
