package javagymrat.paymentservice.model.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class BankAccountValidationException  extends RuntimeException{
    private final List<String> errors;

    public BankAccountValidationException(List<String> errors) {
        super("Bank account validation failed");
        this.errors = errors;
    }

}