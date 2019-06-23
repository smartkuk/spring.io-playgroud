package smart.kuk.person;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
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

  @Test
  public void test_auto_modified_date() {

    Person person = new Person("kuk", 10);
    Person saved = personRepository.save(person);

    assertThat(saved.isNew()).isFalse();
    assertThat(saved.getCreatedBy()).isEqualTo("SYSTEM");

    LocalDateTime before = saved.getLastModifiedDate();

    log.info("test");

    saved.setAge(50);
    Person afterSaving = personRepository.save(saved);

    log.info("past last modified date: {}", before);
    log.info("now last modified date: {}", afterSaving.getLastModifiedDate());

    assertThat(before.isBefore(afterSaving.getLastModifiedDate())).isTrue();
  }
}
