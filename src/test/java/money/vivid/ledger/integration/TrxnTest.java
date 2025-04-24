package money.vivid.ledger.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.google.type.Date;
import com.google.type.Money;
import io.grpc.Status.Code;
import io.grpc.StatusRuntimeException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.Currency;

import money.vivid.ledger.common.Amount;
import money.vivid.ledger.integration.helpers.TAccount;
import money.vivid.ledger.integration.helpers.TTrxn;
import money.vivid.ledger.v1.CreateAccountRequest;
import org.junit.jupiter.api.Test;
import vivid.ledger.v1.TransactionApiOuterClass.CreateTransactionRequest;

public class TrxnTest extends AbstractTest {

  @Test
  void createTransactionIsIdempotent() {
    grpcFixture.createAccount(
        CreateAccountRequest.newBuilder().setAccountId("account-id").setCurrency("USD").build());

    var createTrxn =
        CreateTransactionRequest.newBuilder()
            .setOperationId("operation-id")
            .setValueDate(Date.newBuilder().setYear(2020).setMonth(1).setDay(1).build())
            .setAccountId("account-id")
            .setAmount(Money.newBuilder().setUnits(1).setCurrencyCode("USD").build())
            .build();
    grpcFixture.createTrxn(createTrxn);
    grpcFixture.createTrxn(createTrxn); // check for idempotency

    grpcFixture.checkBalance("account-id", new Amount("USD", new BigDecimal("1.00")));
    dbFixture.checkAccount(
        TAccount.builder()
            .externalId("account-id")
            .balance(new Amount("USD", new BigDecimal("1.00")))
            .updatedAtIsSet(true)
            .build());
    dbFixture.checkTrxn(
        TTrxn.builder()
            .amount(new Amount("USD", new BigDecimal("1.00")))
            .valueDate(LocalDate.of(2020, Month.JANUARY, 1))
            .operationId("operation-id")
            .build());
  }

  @Test
  void createTransactionWithDifferentCurrencyFails() {
    grpcFixture.createAccount(
      CreateAccountRequest.newBuilder().setAccountId("account-id").setCurrency("RUB").build());

    var createTrxn =
      CreateTransactionRequest.newBuilder()
        .setOperationId("operation-id-1")
        .setValueDate(Date.newBuilder().setYear(2020).setMonth(1).setDay(1).build())
        .setAccountId("account-id")
        .setAmount(Money.newBuilder().setUnits(1).setCurrencyCode("USD").build())
        .build();

    try {
      grpcFixture.createTrxn(createTrxn);
    } catch (StatusRuntimeException e) {
      assertThat(e.getStatus().getCode()).isSameAs(Code.INVALID_ARGUMENT);
    }
  }

  @Test
  void accountVersionIncrementsOnEveryBalanceUpdate() {
    /* 0) создаём счёт */
    grpcFixture.createAccount(
      CreateAccountRequest.newBuilder()
        .setAccountId("acc-ver")
        .setCurrency("USD")
        .build());

    /* 1) первая транзакция */
    grpcFixture.createTrxn(txn("op-1", 1));

    /* 2) вторая транзакция */
    grpcFixture.createTrxn(txn("op-2", 1));

    /* 3) проверяем баланс и версию */
    dbFixture.checkAccount(
      TAccount.builder()
        .externalId("acc-ver")
        .balance(new Amount("USD", new BigDecimal("2.00")))
        .version(2)                 // <-- ДОЛЖНО быть «2»: 0→1→2
        .updatedAtIsSet(true)
        .build());

    /* 4) gRPC-чтение баланса тоже ок */
    grpcFixture.checkBalance("acc-ver",
      new Amount("USD", new BigDecimal("2.00")));
  }

  /* вспомогалка: собираем CreateTransactionRequest на лету */
  private static CreateTransactionRequest txn(String opId, long units) {
    return CreateTransactionRequest.newBuilder()
      .setOperationId(opId)
      .setValueDate(Date.newBuilder().setYear(2020).setMonth(1).setDay(1).build())
      .setAccountId("acc-ver")
      .setAmount(Money.newBuilder().setUnits(units).setCurrencyCode("USD").build())
      .build();
  }

  @Test
  void createTransactionWithoutAnAccount() {
    var createTrxn =
        CreateTransactionRequest.newBuilder()
            .setOperationId("operation-id")
            .setValueDate(Date.newBuilder().setYear(2020).setMonth(1).setDay(1).build())
            .setAccountId("account-id")
            .setAmount(Money.newBuilder().setUnits(1).setCurrencyCode("USD").build())
            .build();

    try {
      grpcFixture.createTrxn(createTrxn);
    } catch (StatusRuntimeException e) {
      assertThat(e.getStatus().getCode()).isSameAs(Code.NOT_FOUND);
    }
  }
}
