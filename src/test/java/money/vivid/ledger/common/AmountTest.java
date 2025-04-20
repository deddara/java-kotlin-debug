package money.vivid.ledger.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.Currency;
import org.junit.jupiter.api.Test;

class AmountTest {

  @Test
  void constructorNoExceptionTests() {
    assertThatCode(() -> new Amount("EUR", new BigDecimal("0.00"))).doesNotThrowAnyException();
    assertThatCode(() -> new Amount("EUR", new BigDecimal("1.00"))).doesNotThrowAnyException();
    assertThatCode(() -> new Amount("USD", new BigDecimal("-1"))).doesNotThrowAnyException();
    assertThatCode(() -> new Amount("JPY", new BigDecimal("-1"))).doesNotThrowAnyException();
  }

  @Test
  void constructorWithExceptionTests() {
    assertThatThrownBy(() -> new Amount((Currency) null, new BigDecimal("0.00")))
        .withFailMessage("Amount without currency must not be created")
        .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> new Amount("EUR", null))
        .withFailMessage("Amount without its value must not be created")
        .isInstanceOf(NullPointerException.class);
    assertThatThrownBy(() -> new Amount("WAT", null))
        .withFailMessage("Amount with unknown currency must not be created")
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void zeroTests() {
    assertThat(Amount.zero("EUR").amount()).isZero();
    assertThat(Amount.zero(Currency.getInstance("EUR")).amount()).isZero();
  }

  @Test
  void addTests() {
    var eur0 = new Amount("EUR", new BigDecimal("0.00"));
    var eur1 = new Amount("EUR", new BigDecimal("1.00"));
    var eur2_5 = new Amount("EUR", new BigDecimal("2.50"));
    var usd0 = new Amount("USD", new BigDecimal("0.00"));
    assertThat(eur0.add(eur1)).isEqualTo(new Amount("EUR", new BigDecimal("1.00")));
    assertThat(eur1.add(eur2_5)).isEqualTo(new Amount("EUR", new BigDecimal("3.50")));
    assertThatThrownBy(() -> eur0.add(usd0))
        .withFailMessage("One can't add amounts in different currencies")
        .isInstanceOf(IllegalArgumentException.class);
  }
}
