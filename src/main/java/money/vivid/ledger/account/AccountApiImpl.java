package money.vivid.ledger.account;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import money.vivid.ledger.common.CommonMapper;
import money.vivid.ledger.v1.AccountApiGrpc.AccountApiImplBase;
import money.vivid.ledger.v1.CreateAccountRequest;
import money.vivid.ledger.v1.CreateAccountResponse;
import money.vivid.ledger.v1.GetAccountBalanceRequest;
import money.vivid.ledger.v1.GetAccountBalanceResponse;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@Slf4j
public class AccountApiImpl extends AccountApiImplBase {

  private final AccountsRepository accounts;

  public AccountApiImpl(AccountsRepository accounts) {
    this.accounts = accounts;
  }

  @Override
  public void createAccount(
      CreateAccountRequest request, StreamObserver<CreateAccountResponse> responseObserver) {
    accounts
        .findByExternalId(request.getAccountId())
        .ifPresentOrElse(
            account -> log.debug("Account {} was already created", account.externalId()),
            () -> accounts.save(Account.create(request.getAccountId(), request.getCurrency())));

    responseObserver.onNext(CreateAccountResponse.getDefaultInstance());
    responseObserver.onCompleted();
  }

  @Override
  public void getAccountBalance(
      GetAccountBalanceRequest request,
      StreamObserver<GetAccountBalanceResponse> responseObserver) {
    accounts
        .findByExternalId(request.getAccountId())
        .ifPresentOrElse(
            account -> {
              responseObserver.onNext(
                  GetAccountBalanceResponse.newBuilder()
                      .setBalance(CommonMapper.money(account.balance()))
                      .build());
              responseObserver.onCompleted();
            },
            () ->
                responseObserver.onError(
                    Status.NOT_FOUND
                        .withDescription("Account not found by id: " + request.getAccountId())
                        .asRuntimeException()));
  }
}
