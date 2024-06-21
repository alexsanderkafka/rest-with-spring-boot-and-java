package kafka.system.RestApi.controller;

import kafka.system.RestApi.model.Person;
import kafka.system.RestApi.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/person")
public class PersonController {

    @Autowired
    private PersonService personService;

    @GetMapping(value = "/{id}")
    public Person findById(@PathVariable(value = "id") String id) throws Exception {

        return personService.findById(id);
    }

    @GetMapping()
    public List<Person> findAll() throws Exception {

        return personService.findAll();
    }

    @PostMapping()
    public Person create(@RequestBody Person person) throws Exception {
        return personService.create(person);
    }

    @PutMapping
    public Person update(@RequestBody Person person) throws Exception {
        return personService.update(person);
    }

    @DeleteMapping(value = "/{id}")
    public void delete(@PathVariable(value = "id") String id) throws Exception {
        personService.delete(id);
    }


}
