package money.vivid.ledger.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.type.Date;
import com.google.type.Money;
import io.grpc.Status.Code;
import io.grpc.StatusRuntimeException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
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
      assertThat(e.getStatus().getCode()).isSameAs(Code.FAILED_PRECONDITION);
    }
  }
}
