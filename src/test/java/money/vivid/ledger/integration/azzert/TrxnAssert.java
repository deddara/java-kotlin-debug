package money.vivid.ledger.integration.azzert;

import static org.assertj.core.util.BigDecimalComparator.BIG_DECIMAL_COMPARATOR;

import java.math.BigDecimal;
import money.vivid.ledger.integration.helpers.TTrxn;
import money.vivid.ledger.trxn.Trxn;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;

public class TrxnAssert {

  private final Trxn actual;

  public TrxnAssert(Trxn actual) {
    this.actual = actual;
  }

  public static TrxnAssert assertThat(Trxn actual) {
    return new TrxnAssert(actual);
  }

  public void matches(TTrxn expected) {
    SoftAssertions.assertSoftly(
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
              .extracting(Trxn::id)
              .withFailMessage("Expecting id surrogate key to be always set")
              .isNotNull();
          softly
              .assertThat(actual)
              .extracting(Trxn::createdAt)
              .withFailMessage("Expected createdAt to be set")
              .isNotNull();
        });
  }
}
