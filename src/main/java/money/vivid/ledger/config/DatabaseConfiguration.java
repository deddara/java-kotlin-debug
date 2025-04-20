package money.vivid.ledger.config;

import java.util.Currency;
import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;
import org.springframework.data.jdbc.repository.config.EnableJdbcAuditing;
import org.springframework.lang.NonNull;

@Configuration
@EnableJdbcAuditing(modifyOnCreate = false)
class DatabaseConfiguration extends AbstractJdbcConfiguration {

  @Override
  @NonNull
  protected List<?> userConverters() {
    return List.of(new ToCurrencyConverter(), new FromCurrencyConverter());
  }
}

@ReadingConverter
class ToCurrencyConverter implements Converter<String, Currency> {

  @Override
  public Currency convert(@NonNull String source) {
    return Currency.getInstance(source);
  }
}

@WritingConverter
class FromCurrencyConverter implements Converter<Currency, String> {

  @Override
  public String convert(@NonNull Currency source) {
    return source.getCurrencyCode();
  }
}
