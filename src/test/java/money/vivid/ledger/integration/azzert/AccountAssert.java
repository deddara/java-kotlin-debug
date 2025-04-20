package money.vivid.ledger.integration.azzert;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.assertj.core.util.BigDecimalComparator.BIG_DECIMAL_COMPARATOR;

import java.math.BigDecimal;
import money.vivid.ledger.account.Account;
import money.vivid.ledger.integration.helpers.TAccount;
import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;

public class AccountAssert {

  private final Account actual;

  public AccountAssert(Account actual) {
    this.actual = actual;
  }

  public static AccountAssert assertThat(Account actual) {
    return new AccountAssert(actual);
  }

  public void matches(TAccount expected) {
    assertSoftly(
        softly -> {
          softly
              .assertThat(actual)
              .usingRecursiveComparison(
                  RecursiveComparisonConfiguration.builder()
                      .withIgnoreAllExpectedNullFields(true)
                      .withComparatorForType(BIG_DECIMAL_COMPARATOR, BigDecimal.class)
                      .build())
              .isEqualTo(expected);
          softly
              .assertThat(actual)
              .extracting(Account::id)
              .withFailMessage("Expecting id surrogate key to be always set")
              .isNotNull();
          softly
              .assertThat(actual)
              .extracting(Account::createdAt)
              .withFailMessage("Expected createdAt to be set")
              .isNotNull();
          if (expected.updatedAtIsSet()) {
            softly
                .assertThat(actual)
                .extracting(Account::updatedAt)
                .withFailMessage("Expected updatedAt to be set")
                .isNotNull();
          } else {
            softly
                .assertThat(actual)
                .extracting(Account::updatedAt)
                .withFailMessage("Expected updatedAt not to be set but was %s", actual.updatedAt())
                .isNull();
          }
        });
  }
}
