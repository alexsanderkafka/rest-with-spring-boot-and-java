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
import kafka.system.RestApi.Integrationtest.vo.BookVO;
import kafka.system.RestApi.Integrationtest.vo.PersonVO;
import kafka.system.RestApi.Integrationtest.vo.wrappers.WrapperBookVO;
import kafka.system.RestApi.configs.TestConfigs;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookControllerJsonTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;
    private static ObjectMapper objectMapper;

    private static BookVO bookVO;

    @BeforeAll
    public static void setup(){
        objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        bookVO = new BookVO();
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
                .setBasePath("/api/book/v1")
                .setPort(TestConfigs.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();
    }


    @Test
    @Order(1)
    public void testCreate() throws IOException, ParseException {
        mockBook();

        var content =
                given().spec(specification)
                        .contentType(TestConfigs.CONTENT_TYPE_JSON)
                        .body(bookVO)
                        .when()
                        .post()
                        .then()
                        .statusCode(200)
                        .extract()
                        .body().asString();

        BookVO persistedBook = objectMapper.readValue(content, BookVO.class);
        bookVO = persistedBook;

        assertNotNull(persistedBook.getKey());
        assertNotNull(persistedBook.getAuthor());
        assertNotNull(persistedBook.getLaunchDate());
        assertNotNull(persistedBook.getTitle());
        assertNotNull(persistedBook.getPrice());

        assertTrue(persistedBook.getKey() > 0);

        assertEquals("Robert C. Martin", persistedBook.getAuthor());
        assertEquals(80.0, persistedBook.getPrice());
        assertEquals("Clean Code 2", persistedBook.getTitle());
    }

    @Test
    @Order(2)
    public void testUpdate() throws IOException {
        bookVO.setTitle("Clean Architecture");

        var content =
                given().spec(specification)
                        .contentType(TestConfigs.CONTENT_TYPE_JSON)
                        .body(bookVO)
                        .when()
                        .post()
                        .then()
                        .statusCode(200)
                        .extract()
                        .body().asString();

        BookVO persistedBook = objectMapper.readValue(content, BookVO.class);
        bookVO = persistedBook;

        assertNotNull(persistedBook.getKey());
        assertNotNull(persistedBook.getAuthor());
        assertNotNull(persistedBook.getLaunchDate());
        assertNotNull(persistedBook.getTitle());
        assertNotNull(persistedBook.getPrice());

        assertEquals(bookVO.getKey(), persistedBook.getKey());

        assertEquals("Robert C. Martin", persistedBook.getAuthor());
        assertEquals(80.0, persistedBook.getPrice());
        assertEquals("Clean Architecture", persistedBook.getTitle());
    }


    @Test
    @Order(3)
    public void testFindById() throws IOException {
        var content =
                given().spec(specification)
                        .contentType(TestConfigs.CONTENT_TYPE_JSON)
                        .pathParams("id", bookVO.getKey())
                        .when()
                        .get("{id}")
                        .then()
                        .statusCode(200)
                        .extract()
                        .body().asString();

        BookVO persistedBook = objectMapper.readValue(content, BookVO.class);
        bookVO = persistedBook;

        assertNotNull(persistedBook.getKey());
        assertNotNull(persistedBook.getAuthor());
        assertNotNull(persistedBook.getLaunchDate());
        assertNotNull(persistedBook.getTitle());
        assertNotNull(persistedBook.getPrice());

        assertEquals(bookVO.getKey(), persistedBook.getKey());

        assertEquals("Robert C. Martin", persistedBook.getAuthor());
        assertEquals(80.0, persistedBook.getPrice());
        assertEquals("Clean Architecture", persistedBook.getTitle());
    }

    @Test
    @Order(4)
    public void testDelete() throws IOException {
        given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
                .pathParams("id", bookVO.getKey())
                .when()
                .delete("{id}")
                .then()
                .statusCode(204);
    }

    @Test
    @Order(5)
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

        WrapperBookVO wrapper = objectMapper.readValue(content, WrapperBookVO.class);
        var book = wrapper.getEmbedded().getBooks();

        BookVO findBook = book.getFirst();

        assertNotNull(findBook.getKey());
        assertNotNull(findBook.getAuthor());
        assertNotNull(findBook.getLaunchDate());
        assertNotNull(findBook.getTitle());
        assertNotNull(findBook.getPrice());

        assertEquals(15, findBook.getKey());
    }

    @Test
    @Order(6)
    public void testFindAllWithoutToken() throws IOException {
        RequestSpecification specificationWithoutToken = new RequestSpecBuilder()
                .setBasePath("/api/book/v1")
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
    @Order(7)
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

        assertTrue(content.contains("\"_links\":{\"self\":{\"href\":\"http://localhost:8888/api/book/v1/15\"}}}"));
        assertTrue(content.contains("\"_links\":{\"self\":{\"href\":\"http://localhost:8888/api/book/v1/2\"}}}"));
        assertTrue(content.contains("\"_links\":{\"self\":{\"href\":\"http://localhost:8888/api/book/v1/7\"}}}"));

        assertTrue(content.contains("\"page\":{\"size\":10,\"totalElements\":15,\"totalPages\":2,\"number\":0}}"));

        assertTrue(content.contains("\"last\":{\"href\":\"http://localhost:8888/api/book/v1?direction=asc&page=1&size=10&sort=author,asc\"}}"));
        assertTrue(content.contains("\"next\":{\"href\":\"http://localhost:8888/api/book/v1?direction=asc&page=1&size=10&sort=author,asc\"}"));
        assertTrue(content.contains("\"self\":{\"href\":\"http://localhost:8888/api/book/v1?direction=asc&page=0&size=10&sort=author,asc\"}"));
        assertTrue(content.contains("\"first\":{\"href\":\"http://localhost:8888/api/book/v1?direction=asc&page=0&size=10&sort=author,asc\"}"));
    }


    private void mockBook() throws ParseException {
        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");

        bookVO.setAuthor("Robert C. Martin");
        bookVO.setLaunchDate(date.parse("2009-08-29 13:50:05.878000"));
        bookVO.setPrice(80.0);
        bookVO.setTitle("Clean Code 2");
    }
}
