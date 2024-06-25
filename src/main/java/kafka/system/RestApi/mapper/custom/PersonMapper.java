package kafka.system.RestApi.mapper.custom;

import kafka.system.RestApi.data.vo.v2.PersonVOV2;
import kafka.system.RestApi.model.Person;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class PersonMapper {

    public PersonVOV2 convertEntityToVo(Person person){
        PersonVOV2  vo = new PersonVOV2();
        vo.setId(person.getId());
        vo.setFirstName(person.getFirstName());
        vo.setLastName(person.getLastName());
        vo.setAddress(person.getAddress());
        vo.setGender(person.getGender());
        vo.setBirthDay(new Date());

        return vo;
    }

    public Person convertVoToEntity(PersonVOV2 personV2){
        Person entity = new Person();
        entity.setId(personV2.getId());
        entity.setFirstName(personV2.getFirstName());
        entity.setLastName(personV2.getLastName());
        entity.setAddress(personV2.getAddress());
        entity.setGender(personV2.getGender());
        //person.setBirthDay(new Date());

        return entity;
    }
}
