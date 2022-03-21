package az.unitech.development.account.controller;

import az.unitech.development.account.dto.request.TransferCreateRequest;
import az.unitech.development.account.dto.response.AccountResponse;
import az.unitech.development.account.dto.response.TransferResponse;
import az.unitech.development.account.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    @GetMapping
    public AccountResponse getAllActiveAccountsByCustomerId(
            @RequestHeader(name = "Customer-Id") Long customerId) {
        return accountService.getActiveAccountsByCustomerId(customerId);
    }

    @PostMapping("/transfer")
    public TransferResponse makeTransfer(@RequestHeader(name = "Customer-Id") Long customerId,
                                         @Valid @RequestBody TransferCreateRequest request) {
        return accountService.makeTransfer(customerId, request);
    }

}