package money.vivid.ledger.integration.fixture;

import static money.vivid.ledger.integration.azzert.AmountAssert.assertThat;

import money.vivid.ledger.common.Amount;
import money.vivid.ledger.common.CommonMapper;
import money.vivid.ledger.v1.AccountApiGrpc.AccountApiBlockingStub;
import money.vivid.ledger.v1.CreateAccountRequest;
import money.vivid.ledger.v1.GetAccountBalanceRequest;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import vivid.ledger.v1.TransactionApiGrpc.TransactionApiBlockingStub;
import vivid.ledger.v1.TransactionApiOuterClass.CreateTransactionRequest;

@Component
public class GrpcFixture {

  @GrpcClient("self-client")
  private AccountApiBlockingStub accountApi;

  @GrpcClient("self-client")
  private TransactionApiBlockingStub trxnApi;

  public void createAccount(CreateAccountRequest request) {
    //noinspection ResultOfMethodCallIgnored
    accountApi.createAccount(request);
  }

  public void createTrxn(CreateTransactionRequest request) {
    //noinspection ResultOfMethodCallIgnored
    trxnApi.createTransaction(request);
  }

  public void checkBalance(String accountId, Amount expectedBalance) {
    var actualBalance =
        CommonMapper.amount(
            accountApi
                .getAccountBalance(
                    GetAccountBalanceRequest.newBuilder().setAccountId(accountId).build())
                .getBalance());

    assertThat(actualBalance).isEqualTo(expectedBalance);
  }
}
