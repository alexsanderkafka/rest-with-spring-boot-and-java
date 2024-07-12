package kafka.system.RestApi.Integrationtest.repositories;

import kafka.system.RestApi.Integrationtest.testcontainer.AbstractIntegrationTest;
import kafka.system.RestApi.Integrationtest.vo.PersonVO;
import kafka.system.RestApi.Integrationtest.vo.wrappers.WrapperPersonVO;
import kafka.system.RestApi.configs.TestConfigs;
import kafka.system.RestApi.model.Person;
import kafka.system.RestApi.repositories.PersonRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PersonRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private PersonRepository repository;

    private static Person person;

    @BeforeAll
    public static void setup(){
        person = new Person();
    }

    @Test
    @Order(0)
    public void testFindByName() throws IOException {

        Pageable pageable = PageRequest.of(0, 6, Sort.by(Sort.Direction.ASC, "firstName"));
        person = repository.findPersonsByName("ary", pageable).getContent().getFirst();

        assertNotNull(person.getId());
        assertNotNull(person.getFirstName());
        assertNotNull(person.getLastName());
        assertNotNull(person.getAddress());
        assertNotNull(person.getGender());
        assertTrue(person.getEnabled());


        assertEquals(533, person.getId());
    }

    @Test
    @Order(1)
    public void testDisablePerson() throws IOException {

        repository.disablePerson(person.getId());
        Pageable pageable = PageRequest.of(0, 6, Sort.by(Sort.Direction.ASC, "firstName"));
        person = repository.findPersonsByName("ary", pageable).getContent().getFirst();

        assertNotNull(person.getId());
        assertNotNull(person.getFirstName());
        assertNotNull(person.getLastName());
        assertNotNull(person.getAddress());
        assertNotNull(person.getGender());
        assertFalse(person.getEnabled());

        assertEquals(533, person.getId());
    }

}
