package money.vivid.ledger.integration.fixture;

import static money.vivid.ledger.integration.azzert.AccountAssert.assertThat;
import static money.vivid.ledger.integration.azzert.TrxnAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import money.vivid.ledger.account.Account;
import money.vivid.ledger.account.AccountsRepository;
import money.vivid.ledger.integration.helpers.TAccount;
import money.vivid.ledger.integration.helpers.TTrxn;
import money.vivid.ledger.trxn.Trxn;
import money.vivid.ledger.trxn.TrxnsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DbFixture {

  @Autowired private AccountsRepository accounts;
  @Autowired private TrxnsRepository trxns;

  public void checkAccount(TAccount expected) {
    var actual =
        accounts
            .findByExternalId(expected.externalId())
            .orElseGet(() -> fail("Account " + expected.externalId() + " isn't found"));

    assertThat(actual).matches(expected);
  }

  public void checkTrxn(TTrxn expected) {
    Trxn actual =
        trxns
            .findByOperationId(expected.operationId())
            .orElseGet(() -> fail("Trxn " + expected.operationId() + " isn't found"));
    assertThat(actual).matches(expected);
    if (expected.accountId() != null) {
      long expectedAccountId =
          accounts
              .findByExternalId(expected.accountId())
              .map(Account::id)
              .orElseGet(() -> fail("Account " + expected.accountId() + " isn't found"));
      assertThat(actual.accountId())
          .withFailMessage(
              "Expecting account id of a trxn to be %s but was %s",
              expectedAccountId, actual.accountId())
          .isEqualTo(expectedAccountId);
    }
  }

  public void clearDb() {
    trxns.deleteAll();
    accounts.deleteAll();
  }
}
