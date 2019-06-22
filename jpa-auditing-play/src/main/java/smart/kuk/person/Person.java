package smart.kuk.person;

import lombok.Getter;
import lombok.NoArgsConstructor;
import smart.kuk.config.AbstractAuditEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.UUID;

@Entity
@NoArgsConstructor
@Getter
public class Person extends AbstractAuditEntity {

  @Id
  @GeneratedValue
  private UUID id;

  private String name;

  private Integer age;

  public Person(String name, Integer age) {
    this.name = name;
    this.age = age;
  }
}
