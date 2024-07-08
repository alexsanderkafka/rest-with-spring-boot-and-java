package kafka.system.RestApi.Integrationtest.controller.withyaml;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import kafka.system.RestApi.Integrationtest.controller.withyaml.mapper.YAMLMapper;
import kafka.system.RestApi.Integrationtest.testcontainer.AbstractIntegrationTest;
import kafka.system.RestApi.Integrationtest.vo.AccountCredentialsVO;
import kafka.system.RestApi.Integrationtest.vo.BookVO;
import kafka.system.RestApi.Integrationtest.vo.TokenVO;
import kafka.system.RestApi.configs.TestConfigs;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookControllerYamlTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;
    private static YAMLMapper objectMapper;

    private static BookVO bookVO;

    @BeforeAll
    public static void setup(){
        objectMapper = new YAMLMapper();
        bookVO = new BookVO();
    }

    @Test
    @Order(0)
    public void authorization() {
        AccountCredentialsVO user = new AccountCredentialsVO("leandro", "admin123");

        TokenVO tokenVO =
                given().config(RestAssuredConfig.config()
                                .encoderConfig(
                                        EncoderConfig.encoderConfig().encodeContentTypeAs(
                                                TestConfigs.CONTENT_TYPE_YAML, ContentType.TEXT
                                        )
                                )
                        )
                        .basePath("/auth/signin")
                        .port(TestConfigs.SERVER_PORT)
                        .contentType(TestConfigs.CONTENT_TYPE_YAML)
                        .accept(TestConfigs.CONTENT_TYPE_YAML)
                        .body(user, objectMapper)
                        .when()
                        .post()
                        .then()
                        .statusCode(200).extract().body().as(TokenVO.class, objectMapper);

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

        var persistedBook =
                given().spec(specification)
                        .config(RestAssuredConfig.config()
                                .encoderConfig(
                                        EncoderConfig.encoderConfig().encodeContentTypeAs(
                                                TestConfigs.CONTENT_TYPE_YAML, ContentType.TEXT
                                        )
                                )
                        )
                        .contentType(TestConfigs.CONTENT_TYPE_YAML)
                        .accept(TestConfigs.CONTENT_TYPE_YAML)
                        .body(bookVO, objectMapper)
                        .when()
                        .post()
                        .then()
                        .statusCode(200)
                        .extract()
                        .body().as(BookVO.class, objectMapper);

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

        var persistedBook =
                given().spec(specification)
                        .config(RestAssuredConfig.config()
                                .encoderConfig(
                                        EncoderConfig.encoderConfig().encodeContentTypeAs(
                                                TestConfigs.CONTENT_TYPE_YAML, ContentType.TEXT
                                        )
                                )
                        )
                        .contentType(TestConfigs.CONTENT_TYPE_YAML)
                        .accept(TestConfigs.CONTENT_TYPE_YAML)
                        .body(bookVO, objectMapper)
                        .when()
                        .post()
                        .then()
                        .statusCode(200)
                        .extract()
                        .body().as(BookVO.class, objectMapper);

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
        var persistedBook =
                given().spec(specification)
                        .config(RestAssuredConfig.config()
                                .encoderConfig(
                                        EncoderConfig.encoderConfig().encodeContentTypeAs(
                                                TestConfigs.CONTENT_TYPE_YAML, ContentType.TEXT
                                        )
                                )
                        )
                        .contentType(TestConfigs.CONTENT_TYPE_YAML)
                        .accept(TestConfigs.CONTENT_TYPE_YAML)
                        .pathParams("id", bookVO.getKey())
                        .when()
                        .get("{id}")
                        .then()
                        .statusCode(200)
                        .extract()
                        .body().as(BookVO.class, objectMapper);

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
                .config(RestAssuredConfig.config()
                        .encoderConfig(
                                EncoderConfig.encoderConfig().encodeContentTypeAs(
                                        TestConfigs.CONTENT_TYPE_YAML, ContentType.TEXT
                                )
                        )
                )
                .contentType(TestConfigs.CONTENT_TYPE_YAML)
                .accept(TestConfigs.CONTENT_TYPE_YAML)
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
                        .config(RestAssuredConfig.config()
                                .encoderConfig(
                                        EncoderConfig.encoderConfig().encodeContentTypeAs(
                                                TestConfigs.CONTENT_TYPE_YAML, ContentType.TEXT
                                        )
                                )
                        )
                        .contentType(TestConfigs.CONTENT_TYPE_YAML)
                        .accept(TestConfigs.CONTENT_TYPE_YAML)
                        .when()
                        .get()
                        .then()
                        .statusCode(200)
                        .extract()
                        .body().as(BookVO[].class, objectMapper);

        List<BookVO> book = Arrays.asList(content);

        BookVO findBook = book.getFirst();

        assertNotNull(findBook.getKey());
        assertNotNull(findBook.getAuthor());
        assertNotNull(findBook.getLaunchDate());
        assertNotNull(findBook.getTitle());
        assertNotNull(findBook.getPrice());

        assertEquals(1, findBook.getKey());
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
                        .config(RestAssuredConfig.config()
                                .encoderConfig(
                                        EncoderConfig.encoderConfig().encodeContentTypeAs(
                                                TestConfigs.CONTENT_TYPE_YAML, ContentType.TEXT
                                        )
                                )
                        )
                        .contentType(TestConfigs.CONTENT_TYPE_YAML)
                        .accept(TestConfigs.CONTENT_TYPE_YAML)
                        .when()
                        .get()
                        .then()
                        .statusCode(403);
    }

    private void mockBook() throws ParseException {
        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");

        bookVO.setAuthor("Robert C. Martin");
        bookVO.setLaunchDate(date.parse("2009-08-29 13:50:05.878000"));
        bookVO.setPrice(80.0);
        bookVO.setTitle("Clean Code 2");
    }
}
