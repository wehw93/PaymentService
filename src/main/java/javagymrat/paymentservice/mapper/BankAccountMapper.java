package javagymrat.paymentservice.mapper;

import javagymrat.paymentservice.model.entity.BankAccount;
import org.mapstruct.Mapper;
import org.openapitools.model.BankAccountCreateRequest;
import org.openapitools.model.BankAccountResponse;

@Mapper(componentModel = "spring")
public interface BankAccountMapper {

    BankAccountResponse toDto(BankAccount bankAccount);
    BankAccount toEntity(BankAccountCreateRequest bankAccountResponse);
}
