package kafka.system.RestApi.service;

import kafka.system.RestApi.model.Person;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

@Service
public class PersonService {

    private final AtomicLong counter = new AtomicLong();
    private Logger logger = Logger.getLogger(PersonService.class.getName());

    public Person findById(String id){
        logger.info("Finding one person!");

        Person person = new Person();
        person.setId(counter.incrementAndGet());
        person.setFirstName("Alex");
        person.setLastName("Silva");
        person.setAddress("Rua do pato");
        person.setGender("male");

        return person;
    }

    public List<Person> findAll(){
        logger.info("Finding all people!");


        List<Person> persons = new ArrayList<>();

        for(int i = 0; i < 8; i++){
            Person person  = mockPerson(i);

            persons.add(person);
        }
        return persons;
    }

    public Person create(Person person){
        logger.info("Creating one person!");

        return person;
    }

    public Person update(Person person){
        logger.info("Updating person!");

        return person;
    }

    public void delete(String id){
        logger.info("Deleting person!");
    }

    private Person mockPerson(int i) {
        Person person = new Person();
        person.setId(counter.incrementAndGet());
        person.setFirstName("Person name " + i);
        person.setLastName("Last name " + i);
        person.setAddress("Some address in Brazil " + i);
        person.setGender("Male");

        return person;
    }
}
