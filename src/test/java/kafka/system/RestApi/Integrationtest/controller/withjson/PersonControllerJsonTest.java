package kafka.system.RestApi.Integrationtest.controller.withjson;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import kafka.system.RestApi.Integrationtest.testcontainer.AbstractIntegrationTest;
import kafka.system.RestApi.Integrationtest.vo.AccountCredentialsVO;
import kafka.system.RestApi.Integrationtest.vo.PersonVO;
import kafka.system.RestApi.Integrationtest.vo.wrappers.WrapperPersonVO;
import kafka.system.RestApi.configs.TestConfigs;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PersonControllerJsonTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;
    private static ObjectMapper objectMapper;

    private static PersonVO person;

    @BeforeAll
    public static void setup(){
        objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        person = new PersonVO();
    }

    @Test
    @Order(0)
    public void authorization() {
        AccountCredentialsVO user = new AccountCredentialsVO("leandro", "admin123");

        var accessToken =
                given()
                        .basePath("/auth/signin")
                        .port(TestConfigs.SERVER_PORT)
                        .contentType(TestConfigs.CONTENT_TYPE_JSON)
                        .body(user)
                        .when()
                        .post()
                        .then()
                        .statusCode(200).extract().body().jsonPath().getString("accessToken");

        specification = new RequestSpecBuilder()
                .addHeader(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + accessToken)
                .setBasePath("/api/person/v1")
                .setPort(TestConfigs.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();
    }


    @Test
    @Order(1)
    public void testCreate() throws IOException {
        mockPerson();

        var content =
                given().spec(specification)
                        .contentType(TestConfigs.CONTENT_TYPE_JSON)
                        .body(person)
                        .when()
                        .post()
                        .then()
                        .statusCode(200)
                        .extract()
                        .body().asString();

        PersonVO persistedPerson = objectMapper.readValue(content, PersonVO.class);
        person = persistedPerson;

        assertNotNull(persistedPerson);
        assertNotNull(persistedPerson.getId());
        assertNotNull(persistedPerson.getFirstName());
        assertNotNull(persistedPerson.getLastName());
        assertNotNull(persistedPerson.getAddress());
        assertNotNull(persistedPerson.getGender());
        assertTrue(persistedPerson.getEnabled());

        assertTrue(persistedPerson.getId() > 0);

        assertEquals("Richard", persistedPerson.getFirstName());
        assertEquals("Stallman", persistedPerson.getLastName());
        assertEquals("New York Ciry, New York, US", persistedPerson.getAddress());
        assertEquals("Male", persistedPerson.getGender());
    }

    @Test
    @Order(2)
    public void testUpdate() throws IOException {
        person.setLastName("Senna");

        var content =
                given().spec(specification)
                        .contentType(TestConfigs.CONTENT_TYPE_JSON)
                        .body(person)
                        .when()
                        .put()
                        .then()
                        .statusCode(200)
                        .extract()
                        .body().asString();

        PersonVO persistedPerson = objectMapper.readValue(content, PersonVO.class);
        person = persistedPerson;

        assertNotNull(persistedPerson);
        assertNotNull(persistedPerson.getId());
        assertNotNull(persistedPerson.getFirstName());
        assertNotNull(persistedPerson.getLastName());
        assertNotNull(persistedPerson.getAddress());
        assertNotNull(persistedPerson.getGender());
        assertTrue(persistedPerson.getEnabled());

        assertEquals(person.getId(), persistedPerson.getId());

        assertEquals("Richard", persistedPerson.getFirstName());
        assertEquals("Senna", persistedPerson.getLastName());
        assertEquals("New York Ciry, New York, US", persistedPerson.getAddress());
        assertEquals("Male", persistedPerson.getGender());
    }


    @Test
    @Order(3)
    public void testDisablePersonById() throws IOException {
        var content =
                given().spec(specification)
                        .contentType(TestConfigs.CONTENT_TYPE_JSON)
                        .pathParams("id", person.getId())
                        .when()
                        .patch("{id}")
                        .then()
                        .statusCode(200)
                        .extract()
                        .body().asString();

        PersonVO persistedPerson = objectMapper.readValue(content, PersonVO.class);
        person = persistedPerson;

        assertNotNull(persistedPerson);
        assertNotNull(persistedPerson.getId());
        assertNotNull(persistedPerson.getFirstName());
        assertNotNull(persistedPerson.getLastName());
        assertNotNull(persistedPerson.getAddress());
        assertNotNull(persistedPerson.getGender());
        assertFalse(persistedPerson.getEnabled());

        assertEquals(person.getId(), persistedPerson.getId());

        assertEquals("Richard", persistedPerson.getFirstName());
        assertEquals("New York Ciry, New York, US", persistedPerson.getAddress());
        assertEquals("Male", persistedPerson.getGender());
    }

    @Test
    @Order(4)
    public void testFindById() throws IOException {
        var content =
                given().spec(specification)
                        .contentType(TestConfigs.CONTENT_TYPE_JSON)
                        .pathParams("id", person.getId())
                        .when()
                        .get("{id}")
                        .then()
                        .statusCode(200)
                        .extract()
                        .body().asString();

        PersonVO persistedPerson = objectMapper.readValue(content, PersonVO.class);
        person = persistedPerson;

        assertNotNull(persistedPerson);
        assertNotNull(persistedPerson.getId());
        assertNotNull(persistedPerson.getFirstName());
        assertNotNull(persistedPerson.getLastName());
        assertNotNull(persistedPerson.getAddress());
        assertNotNull(persistedPerson.getGender());

        assertEquals(person.getId(), persistedPerson.getId());

        assertEquals("Richard", persistedPerson.getFirstName());
        assertEquals("New York Ciry, New York, US", persistedPerson.getAddress());
        assertEquals("Male", persistedPerson.getGender());
    }


    @Test
    @Order(5)
    public void testDelete() throws IOException {
        given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .pathParams("id", person.getId())
                .when()
                .delete("{id}")
                .then()
                .statusCode(204);
    }

    @Test
    @Order(6)
    public void testFindAll() throws IOException {
        var content =
                given().spec(specification)
                        .contentType(TestConfigs.CONTENT_TYPE_JSON)
                        .queryParams(
                                "page", 0, "size", 10, "direction", "asc"
                        )
                        .when()
                        .get()
                        .then()
                        .statusCode(200)
                        .extract()
                        .body().asString();

        WrapperPersonVO wrapper = objectMapper.readValue(content, WrapperPersonVO.class);
        var people = wrapper.getEmbedded().getPersons();

        PersonVO findPersonOne = people.get(0);

        assertNotNull(findPersonOne.getId());
        assertNotNull(findPersonOne.getFirstName());
        assertNotNull(findPersonOne.getLastName());
        assertNotNull(findPersonOne.getAddress());
        assertNotNull(findPersonOne.getGender());

        assertEquals(704, findPersonOne.getId());
    }

    @Test
    @Order(7)
    public void testFindByName() throws IOException {
        var content =
                given().spec(specification)
                        .contentType(TestConfigs.CONTENT_TYPE_JSON)
                        .queryParams(
                                "page", 0, "size", 6, "direction", "asc"
                        )
                        .pathParam(
                                "firstName", "ary"
                        )
                        .when()
                        .get("findPersonByName/{firstName}")
                        .then()
                        .statusCode(200)
                        .extract()
                        .body().asString();

        WrapperPersonVO wrapper = objectMapper.readValue(content, WrapperPersonVO.class);
        var people = wrapper.getEmbedded().getPersons();

        PersonVO findPersonOne = people.get(0);

        assertNotNull(findPersonOne.getId());
        assertNotNull(findPersonOne.getFirstName());
        assertNotNull(findPersonOne.getLastName());
        assertNotNull(findPersonOne.getAddress());
        assertNotNull(findPersonOne.getGender());

        assertEquals(533, findPersonOne.getId());
    }


    @Test
    @Order(8)
    public void testFindAllWithoutToken() throws IOException {
        RequestSpecification specificationWithoutToken = new RequestSpecBuilder()
                .setBasePath("/api/person/v1")
                .setPort(TestConfigs.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();

                given().spec(specificationWithoutToken)
                        .contentType(TestConfigs.CONTENT_TYPE_JSON)
                        .when()
                        .get()
                        .then()
                        .statusCode(403);
    }

    @Test
    @Order(9)
    public void testHATEOAS() throws IOException {
        var content =
                given().spec(specification)
                        .contentType(TestConfigs.CONTENT_TYPE_JSON)
                        .queryParams(
                                "page", 0, "size", 10, "direction", "asc"
                        )
                        .when()
                        .get()
                        .then()
                        .statusCode(200)
                        .extract()
                        .body().asString();

        assertTrue(content.contains("\"_links\":{\"self\":{\"href\":\"http://localhost:8888/api/person/v1/704\"}}}"));
        assertTrue(content.contains("\"_links\":{\"self\":{\"href\":\"http://localhost:8888/api/person/v1/733\"}}}"));
        assertTrue(content.contains("\"_links\":{\"self\":{\"href\":\"http://localhost:8888/api/person/v1/163\"}}}"));

        assertTrue(content.contains("\"page\":{\"size\":10,\"totalElements\":1009,\"totalPages\":101,\"number\":0}}"));

        assertTrue(content.contains("\"last\":{\"href\":\"http://localhost:8888/api/person/v1?direction=asc&page=100&size=10&sort=firstName,asc\"}}"));
        assertTrue(content.contains("\"next\":{\"href\":\"http://localhost:8888/api/person/v1?direction=asc&page=1&size=10&sort=firstName,asc\"}"));
        assertTrue(content.contains("\"self\":{\"href\":\"http://localhost:8888/api/person/v1?page=0&size=10&direction=asc\"}"));
        assertTrue(content.contains("\"first\":{\"href\":\"http://localhost:8888/api/person/v1?direction=asc&page=0&size=10&sort=firstName,asc\"}"));
    }


    private void mockPerson() {
        person.setFirstName("Richard");
        person.setLastName("Stallman");
        person.setAddress("New York Ciry, New York, US");
        person.setGender("Male");
        person.setEnabled(true);
    }
}
