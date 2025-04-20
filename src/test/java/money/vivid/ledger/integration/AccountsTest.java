package money.vivid.ledger.integration;

import money.vivid.ledger.common.Amount;
import money.vivid.ledger.integration.helpers.TAccount;
import money.vivid.ledger.v1.CreateAccountRequest;
import org.junit.jupiter.api.Test;

public class AccountsTest extends AbstractTest {

  @Test
  void createAccountIsIdempotent() {
    var createRequest =
        CreateAccountRequest.newBuilder().setAccountId("account-id").setCurrency("USD").build();
    grpcFixture.createAccount(createRequest);
    grpcFixture.createAccount(createRequest); // check for idempotency

    dbFixture.checkAccount(
        TAccount.builder().externalId("account-id").balance(Amount.zero("USD")).build());
    grpcFixture.checkBalance("account-id", Amount.zero("USD"));
  }
}
