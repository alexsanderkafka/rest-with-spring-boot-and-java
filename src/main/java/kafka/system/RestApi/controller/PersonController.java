package kafka.system.RestApi.controller;

import kafka.system.RestApi.data.vo.v1.PersonVO;
import kafka.system.RestApi.data.vo.v2.PersonVOV2;
import kafka.system.RestApi.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/person")
public class PersonController {

    @Autowired
    private PersonService personService;

    @GetMapping(value = "/{id}")
    public PersonVO findById(@PathVariable(value = "id") Long id) throws Exception {

        return personService.findById(id);
    }

    @GetMapping
    public List<PersonVO> findAll() throws Exception {

        return personService.findAll();
    }

    @PostMapping
    public PersonVO create(@RequestBody PersonVO person) throws Exception {
        return personService.create(person);
    }

    @PostMapping("/v2")
    public PersonVOV2 createV2(@RequestBody PersonVOV2 person) throws Exception {
        return personService.createV2(person);
    }

    @PutMapping
    public PersonVO update(@RequestBody PersonVO person) throws Exception {
        return personService.update(person);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> delete(@PathVariable(value = "id") Long id) throws Exception {
        personService.delete(id);
        return ResponseEntity.noContent().build();
    }


}
