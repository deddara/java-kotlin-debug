package money.vivid.ledger.integration;

import money.vivid.ledger.integration.fixture.DbFixture;
import money.vivid.ledger.integration.fixture.GrpcFixture;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AbstractTest {

  @Autowired protected GrpcFixture grpcFixture;
  @Autowired protected DbFixture dbFixture;

  @BeforeEach
  protected void prepareTest() {
    dbFixture.clearDb();
  }
}
