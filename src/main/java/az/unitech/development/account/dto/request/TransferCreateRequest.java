package az.unitech.development.account.dto.request;

import az.unitech.development.account.exception.validation.ErrorMessages;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Data
public class TransferCreateRequest {

    @NotBlank(message = ErrorMessages.FROM_ACCOUNT_NUMBER_NOT_DEFINED)
    private String fromAccountNumber;

    @NotBlank(message = ErrorMessages.TO_ACCOUNT_NUMBER_NOT_DEFINED)
    private String toAccountNumber;

    @DecimalMin(value = "0.0", inclusive = false, message = ErrorMessages.INVALID_AMOUNT)
    private BigDecimal amount;

    public boolean isSameAccount() {
        return fromAccountNumber.equals(toAccountNumber);
    }

}