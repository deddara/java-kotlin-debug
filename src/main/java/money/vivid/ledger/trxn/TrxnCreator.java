package money.vivid.ledger.trxn;

import io.grpc.Status;
import lombok.extern.slf4j.Slf4j;
import money.vivid.ledger.account.AccountsRepository;
import money.vivid.ledger.common.Amount;
import money.vivid.ledger.common.CommonMapper;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vivid.ledger.v1.TransactionApiOuterClass.CreateTransactionRequest;

import java.time.LocalDate;
import java.util.Currency;

@Component
@Slf4j
class TrxnCreator {
  private final TrxnsRepository trxns;
  private final AccountsRepository accounts;

  TrxnCreator(TrxnsRepository trxns, AccountsRepository accounts) {
    this.trxns = trxns;
    this.accounts = accounts;
  }

  private void checkCurrencyMissmatch(Currency c1, Currency c2) throws RuntimeException {
    if (!c1.equals(c2)) {
      throw Status.INVALID_ARGUMENT.withDescription("Currency missmatch").asRuntimeException();
    }
  }

  @Transactional
  public void create(String accountId, Amount amount, String operationId, LocalDate valueDate) {
    var account = accounts.findByExternalId(accountId).orElseThrow(() -> Status.NOT_FOUND
      .withDescription("Account " + accountId + " not found")
      .asRuntimeException());

    checkCurrencyMissmatch(amount.currency(), account.balance().currency());

    var existing = trxns.findByOperationId(operationId);
    if (existing.isPresent()) {
      log.info("Trxn already created {}", operationId);
      return;
    }

    Trxn trxn;
    try {
      trxn = trxns.save(Trxn.create(
        operationId,
        valueDate,
        account.id(),
        amount));
    } catch (DuplicateKeyException dup) {
      log.info("DuplicateKey on {}, fetching existing record", operationId);
      throw Status.INTERNAL.withDescription("Concurrent transaction insert failed").asRuntimeException();
    }
    try {
      accounts.save(account.withBalance(account.balance().add(amount)));
    } catch (OptimisticLockingFailureException e) {
      log.warn("Optimistic‐lock conflict on account {}, retry {}", account.id());
      account = accounts.findById(account.id())
        .orElseThrow(() -> Status.INTERNAL
          .withDescription("Account disappeared")
          .asRuntimeException());
    }
  }
}
