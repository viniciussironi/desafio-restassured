package com.devsuperior.dsmovie.controllers;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import com.devsuperior.dsmovie.utils.TokenUtil;
import io.restassured.http.ContentType;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class MovieControllerRA {

    private String adminUsername, adminPassword, clientUsername, clientPassword;
    private String adminToken, cientToken, invalidToken;
    private Long existingId, nonExistingId;
    private Map<String, Object> postMovieInstance;

    @BeforeEach
    void setUp() {
        baseURI = "http://localhost:8080";

        adminUsername = "maria@gmail.com";
        adminPassword = "123456";
        clientUsername = "alex@gmail.com";
        clientPassword = "123456";

        adminToken = TokenUtil.obtainAcessToken(adminUsername, adminPassword);
        cientToken = TokenUtil.obtainAcessToken(clientUsername, clientPassword);
        invalidToken = adminToken + "abcdefg";

        postMovieInstance = new HashMap<>();
    }

    @Test
    public void findAllShouldReturnOkWhenMovieNoArgumentsGiven() {
        existingId = 2L;
        given()
                .get("/movies")
                .then()
                .statusCode(200);
    }

    @Test
    public void findAllShouldReturnPagedMoviesWhenMovieTitleParamIsNotEmpty() {
        given()
                .get("/movies?title={title}", "The Witcher")
                .then()
                .statusCode(200)
                .body("content[0].id", is(1))
                .body("content[0].title", equalTo("The Witcher"))
                .body("content[0].score", is(4.5F))
                .body("content[0].count", is(2))
                .body("content[0].image", equalTo("https://www.themoviedb.org/t/p/w533_and_h300_bestv2/jBJWaqoSCiARWtfV0GlqHrcdidd.jpg"));
    }

    @Test
    public void findByIdShouldReturnMovieWhenIdExists() {
        existingId = 1L;
        given()
                .get("/movies/{id}", existingId)
                .then()
                .statusCode(200)
                .body("id", is(1))
                .body("title", equalTo("The Witcher"))
                .body("score", is(4.5F))
                .body("count", is(2))
                .body("image", equalTo("https://www.themoviedb.org/t/p/w533_and_h300_bestv2/jBJWaqoSCiARWtfV0GlqHrcdidd.jpg"));

    }

    @Test
    public void findByIdShouldReturnNotFoundWhenIdDoesNotExist() {
        nonExistingId = 100L;
        given()
                .get("/movies/{id}", nonExistingId)
                .then()
                .statusCode(404);
    }

    @Test
    public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndBlankTitle() {
        postMovieInstance.put("title", "");
        postMovieInstance.put("score", 0.0);
        postMovieInstance.put("count", 0);
        postMovieInstance.put("image", "https://www.themoviedb.org/t/p/w533_and_h300_bestv2/jBJWaqoSCiARWtfV0GlqHrcdidd.jpg");
        JSONObject newProduct = new JSONObject(postMovieInstance);

        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + adminToken)
                .body(newProduct)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post("/movies")
                .then()
                .statusCode(404);
    }

    @Test
    public void  insertShouldReturnForbiddenWhenClientLogged() {
        postMovieInstance.put("title", "Test Movie");
        postMovieInstance.put("score", 0.0);
        postMovieInstance.put("count", 0);
        postMovieInstance.put("image", "https://www.themoviedb.org/t/p/w533_and_h300_bestv2/jBJWaqoSCiARWtfV0GlqHrcdidd.jpg");
        JSONObject newProduct = new JSONObject(postMovieInstance);

        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + cientToken)
                .body(newProduct)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post("/movies")
                .then()
                .statusCode(403);
    }

    @Test
    public void insertShouldReturnUnauthorizedWhenInvalidToken () {
        postMovieInstance.put("title", "Test Movie");
        postMovieInstance.put("score", 0.0);
        postMovieInstance.put("count", 0);
        postMovieInstance.put("image", "https://www.themoviedb.org/t/p/w533_and_h300_bestv2/jBJWaqoSCiARWtfV0GlqHrcdidd.jpg");
        JSONObject newProduct = new JSONObject(postMovieInstance);

        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + invalidToken)
                .body(newProduct)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post("/movies")
                .then()
                .statusCode(401);
    }

}
