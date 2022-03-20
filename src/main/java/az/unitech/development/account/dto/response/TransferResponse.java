package az.unitech.development.account.dto.response;

import az.unitech.development.account.dto.TransferStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferResponse {

    private String uuid;
    private TransferStatus status;
    private String message;

    public static TransferResponse of(TransferStatus status, String message) {
        return new TransferResponse(UUID.randomUUID().toString(), status, message);
    }

}
