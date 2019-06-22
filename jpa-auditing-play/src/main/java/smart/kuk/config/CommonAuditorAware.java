package smart.kuk.config;

import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

public class CommonAuditorAware implements AuditorAware<String> {

  private static final String SYSTEM = "SYSTEM";

  @Override
  public Optional<String> getCurrentAuditor() {
    return Optional.of(SYSTEM);
  }
}
