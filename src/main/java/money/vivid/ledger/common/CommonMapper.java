package money.vivid.ledger.common;

import static java.math.BigDecimal.ONE;

import com.google.type.Date;
import com.google.type.Money;
import java.math.BigDecimal;
import java.time.LocalDate;

public class CommonMapper {

  public static Amount amount(Money money) {
    return new Amount(
        money.getCurrencyCode(),
        BigDecimal.valueOf(money.getUnits()).add(BigDecimal.valueOf(money.getNanos(), 9)));
  }

  public static Money money(Amount amount) {
    if (amount == null) {
      return Money.getDefaultInstance();
    }
    return Money.newBuilder()
        .setCurrencyCode(amount.currency().getCurrencyCode())
        .setUnits(amount.amount().longValue())
        .setNanos(amount.amount().remainder(ONE).movePointRight(9).intValueExact())
        .build();
  }

  public static LocalDate localDate(Date date) {
    return LocalDate.of(date.getYear(), date.getMonth(), date.getDay());
  }
}
