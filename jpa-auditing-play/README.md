
## jpa auditing

이 프로젝트는 JPA Auditing 설정하고 테스트 하는 내용을 작성한 프로젝트이다.

#### 사전조건

* [Git]이 설치된 환경
* [Gradle] ( Gradle wrapper가 프로젝트에 있기 때문에 옵션 )
* java IDE ( 옵션 )
* [lombok]

JPA Auditing 테스트를 하기 위해서 @SpringBootTest 어노테이션을 사용할 것을 권고한다. 그리고 프로젝트는 Gradle 멀티 모듈로 구성되어 있고 본 프로젝트는 루트 프로젝트의 서브 프로젝트이기 때문에 해당 부분에 대해서만 설명한다.

#### 설정

* build.gradle

  JPA 사용하기 위해서 spring-boot-starter-data-jpa 종속성으로 추가했다.

  ```gradle
  dependencies {
      implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
  }
  ```

* JPA Configuration

  ```java
  // ... 중략 ...
  @EnableJpaAuditing(auditorAwareRef = "commonAuditorAware")
  @Configuration
  public class JpaAuditingConfig {
    @Bean
    public AuditorAware<String> commonAuditorAware() {
      return new CommonAuditorAware();
    }
  }
  ```

  * ```@EnableJpaAuditing(auditorAwareRef = "commonAuditorAware")```

    JPA Auditing 활성화 하고 현재 자기 자신의 Auditor 찾기 위해 사용하는 BEAN을 지정
  
  * ```return new CommonAuditorAware();```

    AuditorAware 인터페이스를 구현한 CommonAuditorAware 클래스의 객체를 생성하여 BEAN 만들었다. 이 BEAN에서 Auditor가 제공된다.

* abstract class

  ```java
  // ... 중략 ...
  @MappedSuperclass
  @EntityListeners(value = {
      AuditingEntityListener.class
  })
  @Getter
  public abstract class AbstractAuditEntity {

    @Column(nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String lastModifiedBy;
  }
  ```

  * ```@EntityListeners(value = { AuditingEntityListener.class })```

    엔티티가 영속화 되거나 갱신될때 auditing 정보를 캡처하기 위해 리스너 지정

  * ```@CreatedDate, @LastModifiedDate, @CreatedBy, @LastModifiedBy```

    생성시간, 변경시간, 생성자, 변경자 정보를 나타내는 어노테이션을 변수에 지정

* Persion class (Entity)

  ```java
  // ... 중략 ...
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
  ```

  특별해 보이지 않는 엔티티 클래스이고 Auditing 관련 설정이 처리된 추상 클래스를 상속 받았다.

#### 테스트

  JpaAuditing 처리되는 동작을 테스트 하기 위해서 ```@SpringBootTest``` 어노테이션을 사용해서 테스트를 진행했다.

  ```java
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
  ```

  간단하게 엔티티를 저장하고 값을 확인해보는 테스트를 위와 같이 코딩하고 실행했다.

[Git]: https://git-scm.com/
[Gradle]: https://gradle.org/
[lombok]: https://projectlombok.org/
