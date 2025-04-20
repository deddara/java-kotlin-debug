package money.vivid.ledger.common;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

public record Amount(Currency currency, BigDecimal amount) {

  public Amount {
    Objects.requireNonNull(currency, "currency");
    Objects.requireNonNull(amount, "amount");
  }

  public Amount(String currency, BigDecimal amount) {
    this(Currency.getInstance(currency), amount);
  }

  public Amount add(Amount augend) {
    assertSameCurrencies(this, augend);
    return new Amount(currency, amount.add(augend.amount));
  }

  public static Amount zero(String currency) {
    return new Amount(currency, BigDecimal.ZERO);
  }

  public static Amount zero(Currency currency) {
    return new Amount(currency, BigDecimal.ZERO);
  }

  private static void assertSameCurrencies(Amount one, Amount another) {
    if (!one.currency.equals(another.currency)) {
      throw new IllegalArgumentException("Currencies differ: %s != %s".formatted(one, another));
    }
  }

  @Override
  public String toString() {
    return amount + currency.getSymbol();
  }
}
