package javagymrat.paymentservice.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import javagymrat.paymentservice.mapper.BankAccountMapper;
import javagymrat.paymentservice.model.entity.BankAccount;
import javagymrat.paymentservice.model.exception.BankAccountValidationException;
import javagymrat.paymentservice.repository.BankAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.model.BankAccountCreateRequest;
import org.openapitools.model.BankAccountResponse;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BankAccountService {
    private final BankAccountRepository bankAccountRepository;
    private final BankAccountMapper bankAccountMapper;

    @Transactional
    public BankAccountResponse findById(@NotNull Long id) {
        var entity = bankAccountRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Bank account with id " + id + " not found")
        );
        return bankAccountMapper.toDto(entity);
    }

    @Transactional
    public Optional<BankAccount> findOptionalById(@NotNull Long id) {
        try {
            return bankAccountRepository.findById(id);
        } catch (EntityNotFoundException e) {
            return Optional.empty();
        }
    }

    @Transactional
    public Optional<BankAccount> findByAccount(String accountNumber) {
        return Optional.ofNullable(bankAccountRepository.findByNumber(accountNumber));
    }


    @Transactional
    public BankAccountResponse save(BankAccountCreateRequest request) {
        if (findByAccount(request.getNumber()).isPresent()) {
            throw new BankAccountValidationException(List.of("Account number " + request.getNumber() + " already exists."));
        }
        var entity = bankAccountRepository.save(
                bankAccountMapper.toEntity(request)
        );
        return bankAccountMapper.toDto(entity);
    }
}
