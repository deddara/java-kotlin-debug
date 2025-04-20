package money.vivid.ledger.trxn;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface TrxnsRepository extends CrudRepository<Trxn, Long> {

  Optional<Trxn> findByOperationId(String operationId);
}
