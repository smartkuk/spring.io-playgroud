package smart.kuk.person;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Persistable;
import smart.kuk.config.AbstractAuditEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.UUID;

@Entity
@NoArgsConstructor
@Getter
public class Person extends AbstractAuditEntity implements Persistable<UUID> {

  @Id
  @GeneratedValue
  private UUID id;

  @Setter
  private String name;

  @Setter
  private Integer age;

  public Person(String name, Integer age) {
    this.name = name;
    this.age = age;
  }

  @Override
  public boolean isNew() {
    return id == null;
  }
}
