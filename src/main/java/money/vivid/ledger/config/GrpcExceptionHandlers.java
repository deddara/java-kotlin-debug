package money.vivid.ledger.config;

import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;

@GrpcAdvice
public class GrpcExceptionHandlers {

  @GrpcExceptionHandler(StatusRuntimeException.class)
  public StatusRuntimeException handle(StatusRuntimeException e) {
    return e;
  }
}
