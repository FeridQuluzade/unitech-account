package az.unitech.development.account.repository;

import az.unitech.development.account.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, String> {

    Optional<Account> findByAccountNumber(String accountNumber);

    Optional<Account> findByAccountNumberAndCustomerId(String accountNumber, Long customerId);

}