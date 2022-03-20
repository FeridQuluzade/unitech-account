package az.unitech.development.account.service;

import az.unitech.development.account.dto.AccountDto;
import az.unitech.development.account.dto.request.TransferCreateRequest;
import az.unitech.development.account.dto.response.AccountResponse;
import az.unitech.development.account.exception.ErrorCodes;
import az.unitech.development.account.exception.ServiceException;
import az.unitech.development.account.mapper.AccountMapper;
import az.unitech.development.account.model.Account;
import az.unitech.development.account.model.AccountStatus;
import az.unitech.development.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    public AccountResponse getActiveAccountsByCustomerId(Long customerId) {
        List<AccountDto> accountDtoList = accountMapper.toAccountDtoList(
                accountRepository.findByCustomerIdAndStatus(customerId, AccountStatus.ACTIVE));
        return AccountResponse.of(accountDtoList);
    }

    @Transactional
    public void makeTransfer(Long customerId, TransferCreateRequest transferCreateRequest) {
        throwIfFromAccountAndToAccountIsSame(transferCreateRequest.isSameAccount());
        Account fromAccount =
                getOrElseThrowFromAccount(customerId, transferCreateRequest.getFromAccountNumber());
        Account toAccount = getOrElseThrowToAccount(transferCreateRequest.getToAccountNumber());
        throwIfAmountExceedFromAccountAmount(transferCreateRequest.getAmount(), fromAccount);
        throwIfAccountClosed(toAccount);
        successTransfer(fromAccount, toAccount, transferCreateRequest.getAmount());
    }

    private void throwIfFromAccountAndToAccountIsSame(boolean status) {
        if (status) {
            throw ServiceException.of(ErrorCodes.ACCOUNT_IS_SAME,
                    "You cannot transfer to the same account");
        }
    }

    private Account getOrElseThrowFromAccount(Long customerId, String fromAccountNumber) {
        return accountRepository.findByAccountNumberAndCustomerId(fromAccountNumber, customerId)
                .orElseThrow(() -> ServiceException.of(ErrorCodes.ACCOUNT_NOT_FOUND,
                        "The account is not yours, customerId: " + customerId));
    }

    private Account getOrElseThrowToAccount(String toAccountNumber) {
        return accountRepository.findByAccountNumber(toAccountNumber)
                .orElseThrow(() -> ServiceException.of(ErrorCodes.ACCOUNT_NOT_FOUND,
                        "Account not found, accountNumber: " + toAccountNumber));
    }

    private void throwIfAmountExceedFromAccountAmount(BigDecimal amount, Account account) {
        if (amount.compareTo(account.getAmount()) > 0) {
            throw ServiceException.of(ErrorCodes.NOT_ENOUGH_AMOUNT,
                    "There is not enough amount in the balance");
        }
    }

    private void throwIfAccountClosed(Account account) {
        if (account.getStatus().equals(AccountStatus.CLOSED)) {
            throw ServiceException.of(ErrorCodes.ACCOUNT_STATUS_CLOSED, "Account closed !");
        }
    }

    private void successTransfer(Account fromAccount, Account toAccount, BigDecimal amount) {
        fromAccount.setAmount(fromAccount.getAmount().subtract(amount));
        accountRepository.save(fromAccount);
        toAccount.setAmount(toAccount.getAmount().add(amount));
        accountRepository.save(toAccount);
    }

}