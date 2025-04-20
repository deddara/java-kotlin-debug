package money.vivid.ledger.trxn;

import java.time.Instant;
import java.time.LocalDate;
import lombok.Builder;
import money.vivid.ledger.common.Amount;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Table;

@Builder
@Table("TRANSACTIONS")
public record Trxn(
    @Id Long id,
    @Embedded.Nullable(prefix = "OPERATION_") Amount amount,
    LocalDate valueDate,
    String operationId,
    long accountId,
    @CreatedDate Instant createdAt) {

  public static Trxn create(
      String operationId, LocalDate valueDate, long accountId, Amount amount) {
    return new Trxn(null, amount, valueDate, operationId, accountId, null);
  }
}
