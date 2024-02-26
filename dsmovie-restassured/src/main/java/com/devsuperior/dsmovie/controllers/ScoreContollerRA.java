package com.devsuperior.dsmovie.controllers;

import com.devsuperior.dsmovie.utils.TokenUtil;
import io.restassured.http.ContentType;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;

public class ScoreContollerRA {

    private String clientUsername, clientPassword;
    private String cientToken;
    private Map<String, Object> putScoreInstance;

    @BeforeEach
    void setUp() {
        baseURI = "http://localhost:8080";

        clientUsername = "alex@gmail.com";
        clientPassword = "123456";

        cientToken = TokenUtil.obtainAcessToken(clientUsername, clientPassword);

        putScoreInstance = new HashMap<>();
    }

    @Test
    public void saveScoreShouldReturnNotFoundWhenMovieIdDoesNotExist() {
        putScoreInstance.put("movieId", 100);
        putScoreInstance.put("score", 4);

        JSONObject newProduct = new JSONObject(putScoreInstance);

        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + cientToken)
                .body(newProduct)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .put("/scores")
                .then()
                .statusCode(404);
    }

    @Test
    public void saveScoreShouldReturnUnprocessableEntityWhenMissingMovieId() {
        putScoreInstance.put("movieId", "");
        putScoreInstance.put("score", 4);

        JSONObject newProduct = new JSONObject(putScoreInstance);

        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + cientToken)
                .body(newProduct)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .put("/scores")
                .then()
                .statusCode(422);
    }

    @Test
    public void saveScoreShouldReturnUnprocessableEntityWhenScoreIsLessThanZero() {
        putScoreInstance.put("movieId", 1);
        putScoreInstance.put("score", 8);

        JSONObject newProduct = new JSONObject(putScoreInstance);

        given()
                .header("Content-type", "application/json")
                .header("Authorization", "Bearer " + cientToken)
                .body(newProduct)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .put("/scores")
                .then()
                .statusCode(422);
    }

}
