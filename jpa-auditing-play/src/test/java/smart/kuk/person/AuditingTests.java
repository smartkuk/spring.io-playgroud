package smart.kuk.person;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class AuditingTests {

  @Autowired
  private PersonRepository personRepository;

  @Test
  public void test_jpa_auditing() {

    LocalDateTime past = LocalDateTime.now();
    Person person = new Person("kuk", 10);
    personRepository.save(person);

    List<Person> persons = personRepository.findAll();

    assertThat(persons).isNotNull();
    assertThat(persons.isEmpty()).isFalse();

    Person firstMan = persons.get(0);

    assertThat(past.isBefore(firstMan.getCreatedDate())).isTrue();
  }
}
