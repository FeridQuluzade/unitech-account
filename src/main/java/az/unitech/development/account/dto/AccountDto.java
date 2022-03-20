package az.unitech.development.account.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountDto {

    private String accountNumber;
    private Long customerId;
    private BigDecimal amount;

}