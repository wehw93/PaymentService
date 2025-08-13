package javagymrat.paymentservice.repository;

import javagymrat.paymentservice.model.entity.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {
    BankAccount findByNumber(String accountNumber);
}
