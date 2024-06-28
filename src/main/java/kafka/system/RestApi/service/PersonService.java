package kafka.system.RestApi.service;

import kafka.system.RestApi.controller.PersonController;
import kafka.system.RestApi.data.vo.v1.PersonVO;
import kafka.system.RestApi.data.vo.v2.PersonVOV2;
import kafka.system.RestApi.exceptions.RequiredObjectIsNullException;
import kafka.system.RestApi.exceptions.ResourceNotFoundException;
import kafka.system.RestApi.mapper.DozerMapper;
import kafka.system.RestApi.mapper.custom.PersonMapper;
import kafka.system.RestApi.model.Person;
import kafka.system.RestApi.controller.PersonController;
import kafka.system.RestApi.repositories.PersonRepostiroty;
import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;

@Service
public class PersonService {

    private final Logger logger = Logger.getLogger(PersonService.class.getName());

    @Autowired
    private PersonMapper custoMapper;

    @Autowired
    private PersonRepostiroty repostiroty;

    public PersonVO findById(Long id) throws Exception {
        logger.info("Finding one person!");

        var entity = repostiroty.findById(id).orElseThrow(() -> new ResourceNotFoundException("No records found for this id"));

        var personVO = DozerMapper.parseObject(entity, PersonVO.class);
        personVO.add(linkTo(methodOn(PersonController.class).findById(id)).withSelfRel());

        return personVO;
    }

    public List<PersonVO> findAll(){

        logger.info("Finding all people!");

        var personsVO = DozerMapper.parseListObjects(repostiroty.findAll(), PersonVO.class);

        personsVO
                .forEach(p -> {
                    try {
                        p.add(linkTo(methodOn(PersonController.class).findById(p.getKey())).withSelfRel());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });

        return personsVO;
    }

    public PersonVO create(PersonVO person) throws Exception {
        if(person == null) throw new RequiredObjectIsNullException();

        logger.info("Creating one person!");

        var entity = DozerMapper.parseObject(person, Person.class);
        var personVO = DozerMapper.parseObject(repostiroty.save(entity), PersonVO.class);

        personVO.add(linkTo(methodOn(PersonController.class).findById(personVO.getKey())).withSelfRel());

        return personVO;
    }


    public PersonVO update(PersonVO person) throws Exception {
        if(person == null) throw new RequiredObjectIsNullException();

        logger.info("Updating person!");

        Person entity = repostiroty.findById(person.getKey()).orElseThrow(() -> new ResourceNotFoundException("No records found for this id"));

        entity.setFirstName(person.getFirstName());
        entity.setLastName(person.getLastName());
        entity.setAddress(person.getAddress());
        entity.setGender(person.getGender());

        var personVO = DozerMapper.parseObject(repostiroty.save(entity), PersonVO.class);
        personVO.add(linkTo(methodOn(PersonController.class).findById(person.getKey())).withSelfRel());
        return personVO;
    }

    public void delete(Long id){
        logger.info("Deleting person!");

        var entity = repostiroty.findById(id).orElseThrow(() -> new ResourceNotFoundException("No records found for this id"));

        repostiroty.delete(entity);
    }


}
