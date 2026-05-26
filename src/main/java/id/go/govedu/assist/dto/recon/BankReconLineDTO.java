package id.go.govedu.assist.dto.recon;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BankReconLineDTO {
    private String partnerReferenceNo;
    private String bankReferenceNo;
    private BigDecimal amount;
    private String bankStatus;
    private String failureReason;
    private LocalDateTime processedDate;
}
