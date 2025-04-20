package money.vivid.ledger.account;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface AccountsRepository extends CrudRepository<Account, Long> {

  Optional<Account> findByExternalId(String externalId);
}
