package kafka.system.RestApi.repositories;

import kafka.system.RestApi.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepostiroty extends JpaRepository<Person, Long> {
}
