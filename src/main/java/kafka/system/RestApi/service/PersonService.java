package kafka.system.RestApi.service;

import kafka.system.RestApi.data.vo.v1.PersonVO;
import kafka.system.RestApi.data.vo.v2.PersonVOV2;
import kafka.system.RestApi.exceptions.ResourceNotFoundException;
import kafka.system.RestApi.mapper.DozerMapper;
import kafka.system.RestApi.mapper.custom.PersonMapper;
import kafka.system.RestApi.model.Person;
import kafka.system.RestApi.repositories.PersonRepostiroty;
import org.springframework.beans.factory.annotation.Autowired;
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

    public PersonVO findById(Long id){
        logger.info("Finding one person!");

        var entity = repostiroty.findById(id).orElseThrow(() -> new ResourceNotFoundException("No records found for this id"));

        return DozerMapper.parseObject(entity, PersonVO.class);
    }

    public List<PersonVO> findAll(){
        logger.info("Finding all people!");

        return DozerMapper.parseListObjects(repostiroty.findAll(), PersonVO.class);
    }

    public PersonVO create(PersonVO person){
        logger.info("Creating one person!");

        var entity = DozerMapper.parseObject(person, Person.class);
        var vo = DozerMapper.parseObject(repostiroty.save(entity), PersonVO.class);

        return vo;
    }

    public PersonVOV2 createV2(PersonVOV2 person) {
        logger.info("Creating one person with v2!");

        var entity = custoMapper.convertVoToEntity(person);
        var vo = custoMapper.convertEntityToVo(repostiroty.save(entity));

        return vo;
    }

    public PersonVO update(PersonVO person){
        logger.info("Updating person!");

        Person entity = repostiroty.findById(person.getId()).orElseThrow(() -> new ResourceNotFoundException("No records found for this id"));

        entity.setFirstName(person.getFirstName());
        entity.setLastName(person.getLastName());
        entity.setAddress(person.getAddress());
        entity.setGender(person.getGender());

        var vo = DozerMapper.parseObject(entity, PersonVO.class);
        return vo;
    }

    public void delete(Long id){
        logger.info("Deleting person!");

        var entity = repostiroty.findById(id).orElseThrow(() -> new ResourceNotFoundException("No records found for this id"));

        repostiroty.delete(entity);
    }


}
