package money.vivid.ledger.trxn;

import io.grpc.Status;
import lombok.extern.slf4j.Slf4j;
import money.vivid.ledger.account.AccountsRepository;
import money.vivid.ledger.common.CommonMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vivid.ledger.v1.TransactionApiOuterClass.CreateTransactionRequest;

@Component
@Slf4j
class TrxnCreator {
  private final TrxnsRepository trxns;
  private final AccountsRepository accounts;

  TrxnCreator(TrxnsRepository trxns, AccountsRepository accounts) {
    this.trxns = trxns;
    this.accounts = accounts;
  }

  @Transactional
  public void create(CreateTransactionRequest request) {
    var account = accounts.findByExternalId(request.getAccountId()).orElse(null);
    if (account == null) {
      throw Status.FAILED_PRECONDITION
          .withDescription("Account not found: " + request.getAccountId())
          .asRuntimeException();
    }
    trxns
        .findByOperationId(request.getOperationId())
        .ifPresentOrElse(
            trxn -> log.debug("Trxn already created {}", trxn.operationId()),
            () -> {
              var trxnAmount = CommonMapper.amount(request.getAmount());
              trxns.save(
                  Trxn.create(
                      request.getOperationId(),
                      CommonMapper.localDate(request.getValueDate()),
                      account.id(),
                      trxnAmount));
              accounts.save(account.withBalance(account.balance().add(trxnAmount)));
            });
  }
}
