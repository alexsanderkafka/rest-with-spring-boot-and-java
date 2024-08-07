package kafka.system.RestApi.Integrationtest.controller.withxml;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import kafka.system.RestApi.Integrationtest.testcontainer.AbstractIntegrationTest;
import kafka.system.RestApi.Integrationtest.vo.AccountCredentialsVO;
import kafka.system.RestApi.Integrationtest.vo.BookVO;
import kafka.system.RestApi.Integrationtest.vo.TokenVO;
import kafka.system.RestApi.Integrationtest.vo.pagedmodels.PagedModelBook;
import kafka.system.RestApi.Integrationtest.vo.wrappers.WrapperBookVO;
import kafka.system.RestApi.configs.TestConfigs;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookControllerXmlTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;
    private static XmlMapper objectMapper;

    private static BookVO bookVO;

    @BeforeAll
    public static void setup(){
        objectMapper = new XmlMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        bookVO = new BookVO();
    }

    @Test
    @Order(0)
    public void authorization() {
        AccountCredentialsVO user = new AccountCredentialsVO("leandro", "admin123");

        TokenVO tokenVO =
                given()
                        .basePath("/auth/signin")
                        .port(TestConfigs.SERVER_PORT)
                        .contentType(TestConfigs.CONTENT_TYPE_XML)
                        .accept(TestConfigs.CONTENT_TYPE_XML)
                        .body(user)
                        .when()
                        .post()
                        .then()
                        .statusCode(200).extract().body().as(TokenVO.class);

        specification = new RequestSpecBuilder()
                .addHeader(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + tokenVO.getAccessToken())
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
                        .contentType(TestConfigs.CONTENT_TYPE_XML)
                        .accept(TestConfigs.CONTENT_TYPE_XML)
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
                        .contentType(TestConfigs.CONTENT_TYPE_XML)
                        .accept(TestConfigs.CONTENT_TYPE_XML)
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
                        .contentType(TestConfigs.CONTENT_TYPE_XML)
                        .accept(TestConfigs.CONTENT_TYPE_XML)
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
                .contentType(TestConfigs.CONTENT_TYPE_XML)
                .accept(TestConfigs.CONTENT_TYPE_XML)
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
                        .contentType(TestConfigs.CONTENT_TYPE_XML)
                        .queryParams(
                                "page", 0, "size", 10, "direction", "asc"
                        )
                        .accept(TestConfigs.CONTENT_TYPE_XML)
                        .when()
                        .get()
                        .then()
                        .statusCode(200)
                        .extract()
                        .body().asString();

        PagedModelBook wrapper = objectMapper.readValue(content, PagedModelBook.class);
        var book = wrapper.getContent();

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
                        .contentType(TestConfigs.CONTENT_TYPE_XML)
                        .accept(TestConfigs.CONTENT_TYPE_XML)
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
                        .contentType(TestConfigs.CONTENT_TYPE_XML)
                        .accept(TestConfigs.CONTENT_TYPE_XML)
                        .queryParams(
                                "page", 0, "size", 10, "direction", "asc"
                        )
                        .when()
                        .get()
                        .then()
                        .statusCode(200)
                        .extract()
                        .body().asString();

        assertTrue(content.contains("<links><rel>self</rel><href>http://localhost:8888/api/book/v1/15</href></links>"));
        assertTrue(content.contains("<links><rel>self</rel><href>http://localhost:8888/api/book/v1/7</href></links>"));
        assertTrue(content.contains("<links><rel>self</rel><href>http://localhost:8888/api/book/v1/2</href></links>"));

        assertTrue(content.contains("<page><size>10</size><totalElements>16</totalElements><totalPages>2</totalPages><number>0</number></page>"));

        assertTrue(content.contains("<links><rel>last</rel><href>http://localhost:8888/api/book/v1?direction=asc&amp;page=1&amp;size=10&amp;sort=author,asc</href></links>"));
        assertTrue(content.contains("<links><rel>next</rel><href>http://localhost:8888/api/book/v1?direction=asc&amp;page=1&amp;size=10&amp;sort=author,asc</href></links>"));
        assertTrue(content.contains("<links><rel>self</rel><href>http://localhost:8888/api/book/v1?direction=asc&amp;page=0&amp;size=10&amp;sort=author,asc</href></links>"));
        assertTrue(content.contains("<links><rel>first</rel><href>http://localhost:8888/api/book/v1?direction=asc&amp;page=0&amp;size=10&amp;sort=author,asc</href></links>"));
    }


    private void mockBook() throws ParseException {
        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");

        bookVO.setAuthor("Robert C. Martin");
        bookVO.setLaunchDate(date.parse("2009-08-29 13:50:05.878000"));
        bookVO.setPrice(80.0);
        bookVO.setTitle("Clean Code 2");
    }
}
