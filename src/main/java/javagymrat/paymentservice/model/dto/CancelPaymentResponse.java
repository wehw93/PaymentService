package javagymrat.paymentservice.model.dto;

import javagymrat.paymentservice.model.dto.enums.CommandResultStatus;
import lombok.Data;

@Data
public class CancelPaymentResponse {
    private CommandResultStatus status;
    private String errorMessage;
}
