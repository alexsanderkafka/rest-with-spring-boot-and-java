package kafka.system.RestApi.Integrationtest.controller.withjson;

import kafka.system.RestApi.Integrationtest.testcontainer.AbstractIntegrationTest;
import kafka.system.RestApi.Integrationtest.vo.AccountCredentialsVO;
import kafka.system.RestApi.Integrationtest.vo.TokenVO;
import kafka.system.RestApi.configs.TestConfigs;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.JsonMappingException;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthControllerJsonTest extends AbstractIntegrationTest {

    private static TokenVO tokenVO;

    @Test
    @Order(1)
    public void testSignin() throws JsonMappingException, JsonProcessingException, ParseException {
        AccountCredentialsVO user = new AccountCredentialsVO("leandro", "admin123");

        tokenVO =
                given()
                        .basePath("/auth/signin")
                        .port(TestConfigs.SERVER_PORT)
                        .contentType(TestConfigs.CONTENT_TYPE_JSON)
                        .body(user)
                        .when()
                        .post()
                        .then()
                        .statusCode(200).extract()
                        .body().as(TokenVO.class);//.jsonPath();

        //SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

        //tokenVO = new TokenVO(response.getString("userName"), response.getBoolean("authenticated"), date.parse(response.getString("created")), date.parse(response.getString("expiration")), response.getString("accessToken"), response.getString("refreshToken"));

        assertNotNull(tokenVO.getAccessToken());
        assertNotNull(tokenVO.getRefreshToken());
    }


    @Test
    @Order(2)
    public void testRefresh() throws JsonMappingException, JsonProcessingException, ParseException {
        AccountCredentialsVO user = new AccountCredentialsVO("leandro", "admin123");

        TokenVO newTokenVO = new TokenVO();

        newTokenVO =
                given()
                        .basePath("/auth/refresh")
                        .port(TestConfigs.SERVER_PORT)
                        .contentType(TestConfigs.CONTENT_TYPE_JSON)
                            .pathParams("username", tokenVO.getUserName())
                            .header(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + tokenVO.getRefreshToken())
                        .when()
                        .put("{username}")
                        .then()
                            .statusCode(200).extract().body().as(TokenVO.class);//.jsonPath();

        //SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

        //newTokenVO = new TokenVO(response.getString("userName"), response.getBoolean("authenticated"), date.parse(response.getString("created")), date.parse(response.getString("expiration")), response.getString("accessToken"), response.getString("refreshToken"));

        assertNotNull(newTokenVO.getAccessToken());
        assertNotNull(newTokenVO.getRefreshToken());
    }
}
