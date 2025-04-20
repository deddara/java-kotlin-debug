package money.vivid.ledger.trxn;

import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import vivid.ledger.v1.TransactionApiGrpc.TransactionApiImplBase;
import vivid.ledger.v1.TransactionApiOuterClass.CreateTransactionRequest;
import vivid.ledger.v1.TransactionApiOuterClass.CreateTransactionResponse;

@GrpcService
@Slf4j
public class TrxnApiImpl extends TransactionApiImplBase {

  private final TrxnCreator trxnCreator;

  public TrxnApiImpl(TrxnCreator trxnCreator) {
    this.trxnCreator = trxnCreator;
  }

  @Override
  public void createTransaction(
      CreateTransactionRequest request,
      StreamObserver<CreateTransactionResponse> responseObserver) {
    trxnCreator.create(request);
    responseObserver.onNext(CreateTransactionResponse.getDefaultInstance());
    responseObserver.onCompleted();
  }
}
