package az.unitech.development.account.controller;

import az.unitech.development.account.dto.request.TransferCreateRequest;
import az.unitech.development.account.dto.response.AccountResponse;
import az.unitech.development.account.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping
    public AccountResponse getAllActiveAccountsByCustomerId(@RequestParam Long customerId) {
        return accountService.getActiveAccountsByCustomerId(customerId);
    }

    @PostMapping("/transfer")
    public void makeTransfer(@RequestParam Long customerId,
                             @Valid @RequestBody TransferCreateRequest transferCreateRequest) {
        accountService.makeTransfer(customerId, transferCreateRequest);
    }

}