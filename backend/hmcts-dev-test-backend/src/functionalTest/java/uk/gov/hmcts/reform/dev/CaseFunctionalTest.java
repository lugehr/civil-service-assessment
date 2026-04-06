package uk.gov.hmcts.reform.dev;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import uk.gov.hmcts.reform.dev.models.Case;
import uk.gov.hmcts.reform.dev.repositories.CaseRepository;

import java.time.LocalDate;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CaseFunctionalTest {
    protected static final String CONTENT_TYPE_VALUE = "application/json";

    @LocalServerPort
    private int port;

    @Autowired
    CaseRepository repository;

    private Case courtCase1;
    private Case courtCase2;

    @SuppressWarnings("null")
    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        repository.deleteAll();
        courtCase1 = new Case(null, "Case 1", "Description 1", "Open", LocalDate.now().plusDays(1));
        courtCase2 = new Case(null, "Case 2", "Description 2", "Open", LocalDate.now().plusDays(2));
        repository.save(courtCase1);
        repository.save(courtCase2);
    }

    @Test
    void getByIdTestExists() {
        given()
            .contentType(ContentType.JSON)
        .when()
            .get("/cases/" + courtCase1.getId())
        .then()
            .statusCode(200)
            .body("id", equalTo(courtCase1.getId()));
    }

    @Test
    void getByIdTestNotExists() {
        given()
            .contentType(ContentType.JSON)
        .when()
            .get("/cases/999")
        .then()
            .statusCode(404);
    }

    @Test
    void getAllCasesTest() {
        given()
            .contentType(ContentType.JSON)
        .when()
            .get("/cases")
        .then()
            .statusCode(200)
            .body("size()", equalTo(2))
            .body("title", hasItems("Case 1", "Case 2"));
    }

    @Test
    void getAllCasesEmpty() {
        repository.deleteAll();

        given()
            .contentType(ContentType.JSON)
        .when()
            .get("/cases")
        .then()
            .statusCode(200)
            .body("size()", equalTo(0));
    }

    @Test
    void createCaseTest() {
        Case newCase = new Case(null, "New Case", "New Description", "Open", LocalDate.now().plusDays(3));

        given()
            .contentType(ContentType.JSON)
            .body(newCase)
        .when()
            .post("/cases")
        .then()
            .statusCode(201)
            .body("title", equalTo("New Case"));
    }

    @Test
    void updateCaseStatusTest() {
        given()
            .contentType(ContentType.JSON)
            .body(Map.of("status", "In Progress"))
        .when()
            .patch("/cases/" + courtCase1.getId() + "/status")
        .then()
            .statusCode(200)
            .body("status", equalTo("In Progress"));
    }

    @Test
    void updateCaseStatusTestNotExists() {
        given()
            .contentType(ContentType.JSON)
            .body(Map.of("status", "In Progress"))
        .when()
            .patch("/cases/999/status")
        .then()
            .statusCode(404);
    }

    @Test
    void deleteCaseTest() {
        given()
            .contentType(ContentType.JSON)
        .when()
            .delete("/cases/" + courtCase1.getId())
        .then()
            .statusCode(204);
    }

    @Test
    void deleteCaseTestNotExists() {
        given()
            .contentType(ContentType.JSON)
        .when()
            .delete("/cases/999")
        .then()
            .statusCode(404);
    }
}
