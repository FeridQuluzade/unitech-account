package az.unitech.development.account.service;

import az.unitech.development.account.dto.TransferCreateDto;
import az.unitech.development.account.exception.ErrorCodes;
import az.unitech.development.account.exception.ServiceException;
import az.unitech.development.account.model.Account;
import az.unitech.development.account.model.AccountStatus;
import az.unitech.development.account.repository.AccountRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    private static final Long CUSTOMER_ID = 1L;
    private static final String TO_ACCOUNT_NUMBER = "c29c08f1-505d-4452-81e2-9192beb6bad0";
    private static final String FROM_ACCOUNT_NUMBER = "3a7d1f5d-5b4e-48a6-945d-6c2db7925fd6";

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    @Test
    void makeTransfer_WhenToAccountAndFromAccountIsSame_ShouldThrowServiceException() {
        var transferCreateDto = new TransferCreateDto();
        transferCreateDto.setToAccountNumber(TO_ACCOUNT_NUMBER);
        transferCreateDto.setFromAccountNumber(TO_ACCOUNT_NUMBER);

        ServiceException serviceException = assertThrows(ServiceException.class,
                () -> accountService.makeTransfer(CUSTOMER_ID, transferCreateDto));

        Assertions.assertEquals(ErrorCodes.ACCOUNT_IS_SAME.code(),
                serviceException.getErrorCode());
    }

    @Test
    void makeTransfer_WhenFromAccountNotFound_ShouldThrowServiceException() {
        var transferCreateDto = new TransferCreateDto();
        transferCreateDto.setFromAccountNumber(FROM_ACCOUNT_NUMBER);

        when(accountRepository.findByAccountNumberAndCustomerId(
                transferCreateDto.getFromAccountNumber(), CUSTOMER_ID))
                .thenReturn(Optional.empty());

        ServiceException serviceException = assertThrows(ServiceException.class,
                () -> accountService.makeTransfer(CUSTOMER_ID, transferCreateDto));

        Assertions.assertEquals(
                ErrorCodes.ACCOUNT_NOT_FOUND.code(), serviceException.getErrorCode());
        verify(accountRepository, times(1))
                .findByAccountNumberAndCustomerId(FROM_ACCOUNT_NUMBER, CUSTOMER_ID);
    }

    @Test
    void makeTransfer_WhenToAccountNotFound_ShouldThrowServiceException() {
        var transferCreateDto = new TransferCreateDto();
        transferCreateDto.setToAccountNumber(TO_ACCOUNT_NUMBER);
        transferCreateDto.setFromAccountNumber(FROM_ACCOUNT_NUMBER);

        when(accountRepository.findByAccountNumberAndCustomerId(
                transferCreateDto.getFromAccountNumber(), CUSTOMER_ID))
                .thenReturn(Optional.of(new Account()));
        when(accountRepository.findByAccountNumber(transferCreateDto.getToAccountNumber()))
                .thenReturn(Optional.empty());

        ServiceException serviceException = assertThrows(ServiceException.class,
                () -> accountService.makeTransfer(CUSTOMER_ID, transferCreateDto));

        Assertions.assertEquals(
                ErrorCodes.ACCOUNT_NOT_FOUND.code(), serviceException.getErrorCode());
        verify(accountRepository, times(1))
                .findByAccountNumberAndCustomerId(FROM_ACCOUNT_NUMBER, CUSTOMER_ID);
        verify(accountRepository, times(1))
                .findByAccountNumber(TO_ACCOUNT_NUMBER);
    }

    @Test
    void makeTransfer_WhenAmountExceedFromAccountAmount_ShouldThrowException() {
        var transferCreateDto = new TransferCreateDto();
        transferCreateDto.setFromAccountNumber(FROM_ACCOUNT_NUMBER);
        transferCreateDto.setToAccountNumber(TO_ACCOUNT_NUMBER);
        transferCreateDto.setAmount(BigDecimal.valueOf(500));

        var fromAccount = new Account();
        fromAccount.setAccountNumber(FROM_ACCOUNT_NUMBER);
        fromAccount.setAmount(BigDecimal.valueOf(200));

        var toAccount = new Account();
        toAccount.setAccountNumber(TO_ACCOUNT_NUMBER);

        when(accountRepository.findByAccountNumberAndCustomerId(FROM_ACCOUNT_NUMBER, CUSTOMER_ID))
                .thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByAccountNumber(TO_ACCOUNT_NUMBER))
                .thenReturn(Optional.of(toAccount));

        ServiceException serviceException = assertThrows(ServiceException.class,
                () -> accountService.makeTransfer(CUSTOMER_ID, transferCreateDto));

        Assertions.assertEquals(
                ErrorCodes.NOT_ENOUGH_AMOUNT.code(), serviceException.getErrorCode());
        verify(accountRepository, times(1))
                .findByAccountNumberAndCustomerId(FROM_ACCOUNT_NUMBER, 1L);
        verify(accountRepository, times(1))
                .findByAccountNumber(TO_ACCOUNT_NUMBER);
    }

    @Test
    void makeTransfer_WhenToAccountClosed_ShouldThrowException() {
        var transferCreateDto = new TransferCreateDto();
        transferCreateDto.setFromAccountNumber(FROM_ACCOUNT_NUMBER);
        transferCreateDto.setToAccountNumber(TO_ACCOUNT_NUMBER);
        transferCreateDto.setAmount(BigDecimal.valueOf(200));

        var fromAccount = new Account();
        fromAccount.setAccountNumber(FROM_ACCOUNT_NUMBER);
        fromAccount.setAmount(BigDecimal.valueOf(400));

        var toAccount = new Account();
        toAccount.setAccountNumber(TO_ACCOUNT_NUMBER);
        toAccount.setStatus(AccountStatus.CLOSED);

        when(accountRepository.findByAccountNumberAndCustomerId(FROM_ACCOUNT_NUMBER, CUSTOMER_ID))
                .thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByAccountNumber(TO_ACCOUNT_NUMBER))
                .thenReturn(Optional.of(toAccount));

        ServiceException serviceException = assertThrows(ServiceException.class,
                () -> accountService.makeTransfer(CUSTOMER_ID, transferCreateDto));

        Assertions.assertEquals(
                ErrorCodes.ACCOUNT_STATUS_CLOSED.code(), serviceException.getErrorCode());
        verify(accountRepository, times(1))
                .findByAccountNumberAndCustomerId(FROM_ACCOUNT_NUMBER, CUSTOMER_ID);
        verify(accountRepository, times(1))
                .findByAccountNumber(TO_ACCOUNT_NUMBER);
    }

    @Test
    void makeTransfer_Success() {
        var transferCreateDto = new TransferCreateDto();
        transferCreateDto.setFromAccountNumber(FROM_ACCOUNT_NUMBER);
        transferCreateDto.setToAccountNumber(TO_ACCOUNT_NUMBER);
        transferCreateDto.setAmount(BigDecimal.valueOf(200));

        var fromAccount = new Account();
        fromAccount.setCustomerId(CUSTOMER_ID);
        fromAccount.setAccountNumber(FROM_ACCOUNT_NUMBER);
        fromAccount.setAmount(BigDecimal.valueOf(400));

        var toAccount = new Account();
        toAccount.setAccountNumber(TO_ACCOUNT_NUMBER);
        toAccount.setStatus(AccountStatus.ACTIVE);
        toAccount.setAmount(BigDecimal.valueOf(0));

        fromAccount.setAmount(fromAccount.getAmount().subtract(transferCreateDto.getAmount()));
        toAccount.setAmount(fromAccount.getAmount().add(transferCreateDto.getAmount()));

        when(accountRepository.findByAccountNumberAndCustomerId(FROM_ACCOUNT_NUMBER, CUSTOMER_ID))
                .thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByAccountNumber(TO_ACCOUNT_NUMBER))
                .thenReturn(Optional.of(toAccount));

        accountService.makeTransfer(CUSTOMER_ID, transferCreateDto);
        verify(accountRepository, times(1))
                .findByAccountNumberAndCustomerId(FROM_ACCOUNT_NUMBER, CUSTOMER_ID);
        verify(accountRepository, times(1))
                .findByAccountNumber(TO_ACCOUNT_NUMBER);
    }

}