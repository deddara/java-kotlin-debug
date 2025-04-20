package money.vivid.ledger.account;

import java.time.Instant;
import lombok.With;
import money.vivid.ledger.common.Amount;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Table;

@Table("ACCOUNTS")
@With
public record Account(
    @Id Long id,
    String externalId,
    @Embedded.Nullable(prefix = "BALANCE_") Amount balance,
    @CreatedDate Instant createdAt,
    @LastModifiedDate Instant updatedAt) {

  public static Account create(String externalId, String currency) {
    return new Account(null, externalId, Amount.zero(currency), null, null);
  }
}
