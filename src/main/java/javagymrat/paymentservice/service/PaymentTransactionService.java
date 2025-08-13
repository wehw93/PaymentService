package javagymrat.paymentservice.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import javagymrat.paymentservice.mapper.PaymentTransactionMapper;
import javagymrat.paymentservice.model.dto.CreatePaymentTransactionResponse;
import javagymrat.paymentservice.model.entity.PaymentTransaction;
import javagymrat.paymentservice.repository.PaymentTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.model.PaymentTransactionResponse;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentTransactionService {
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final PaymentTransactionMapper paymentTransactionMapper;

    @Transactional
    public CreatePaymentTransactionResponse save(PaymentTransaction paymentTransaction) {
        var entity = paymentTransactionRepository.save(paymentTransaction);
        return paymentTransactionMapper.toKafkaDto(entity);
    }

    @Transactional
    public Optional<PaymentTransaction> findOptionalById(@NotNull Long id){
        try{
            return paymentTransactionRepository.findById(id);
        } catch (EntityNotFoundException e){
            return Optional.empty();
        }
    }

    @Transactional
    public PaymentTransactionResponse findById(@NotNull Long id){
        var entity = paymentTransactionRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Payment transaction with id " + id + " not found")
        );
        return paymentTransactionMapper.toDto(entity);
    }

}
