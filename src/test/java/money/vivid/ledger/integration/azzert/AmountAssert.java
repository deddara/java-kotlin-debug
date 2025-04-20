package money.vivid.ledger.integration.azzert;

import static org.assertj.core.util.BigDecimalComparator.BIG_DECIMAL_COMPARATOR;

import java.math.BigDecimal;
import money.vivid.ledger.common.Amount;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;

public class AmountAssert {

  private final Amount actual;

  public AmountAssert(Amount actual) {
    this.actual = actual;
  }

  public static AmountAssert assertThat(Amount actual) {
    return new AmountAssert(actual);
  }

  public void isEqualTo(Amount expected) {
    Assertions.assertThat(actual)
        .usingRecursiveComparison(
            RecursiveComparisonConfiguration.builder()
                .withComparatorForType(BIG_DECIMAL_COMPARATOR, BigDecimal.class)
                .build())
        .isEqualTo(expected);
  }
}
