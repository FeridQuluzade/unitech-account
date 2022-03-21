package az.unitech.development.account.dto.request;

import az.unitech.development.account.error.validation.ErrorMessages;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
public class TransferCreateRequest {

    @NotBlank(message = ErrorMessages.FROM_ACCOUNT_NUMBER_NOT_DEFINED)
    private String fromAccountNumber;

    @NotBlank(message = ErrorMessages.TO_ACCOUNT_NUMBER_NOT_DEFINED)
    private String toAccountNumber;

    @Positive(message = ErrorMessages.INVALID_AMOUNT)
    private BigDecimal amount;

    public boolean isSameAccount() {
        return fromAccountNumber.equals(toAccountNumber);
    }

}