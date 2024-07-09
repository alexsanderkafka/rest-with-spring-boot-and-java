package kafka.system.RestApi.repositories;

import kafka.system.RestApi.model.Person;
import kafka.system.RestApi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {

    @Modifying
    @Query("""
            UPDATE Person p
            SET p.enabled = false
            WHERE p.id =:id
            """)
    void disablePerson(@Param("id") Long id);
}
