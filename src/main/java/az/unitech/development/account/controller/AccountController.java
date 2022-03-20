package az.unitech.development.account.controller;

import az.unitech.development.account.dto.TransferCreateDto;
import az.unitech.development.account.model.Account;
import az.unitech.development.account.repository.AccountRepository;
import az.unitech.development.account.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;
    private final AccountRepository accountRepository;

    @PostMapping("/transfer")
    public void makeTransfer(@RequestParam Long customerId,
                             @Valid @RequestBody TransferCreateDto transferCreateDto) {
        accountService.makeTransfer(customerId, transferCreateDto);
    }

    @PostMapping
    public Account createAccount(@RequestBody Account account) {
        return accountRepository.save(account);
    }

}