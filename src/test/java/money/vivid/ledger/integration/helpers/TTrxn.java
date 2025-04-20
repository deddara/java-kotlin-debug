package money.vivid.ledger.integration.helpers;

import java.time.LocalDate;
import lombok.Builder;
import money.vivid.ledger.common.Amount;

@Builder
public record TTrxn(Amount amount, LocalDate valueDate, String operationId, String accountId) {}
