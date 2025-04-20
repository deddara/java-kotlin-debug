package money.vivid.ledger.integration.helpers;

import lombok.Builder;
import money.vivid.ledger.common.Amount;

@Builder
public record TAccount(String externalId, Amount balance, boolean updatedAtIsSet) {}
