import DTO.UserRequestDTO;
import DTO.UserResponseDTO;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class UserTest {
    private static RequestSpecification spec;

    @BeforeAll
    public static void setupSpec(){
        spec = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .addHeader("Authorization", "Bearer S4nungQWB_-O1hiKAfHEkeIVfX1LjC41HYA0")
                .setBaseUri("https://gorest.co.in/public-api")
                .addFilter(new ResponseLoggingFilter())
                .addFilter(new RequestLoggingFilter())
                .build();
    }

    @Test
    public void createUserAndCheckExistence(){
        UserRequestDTO newUser = createDummyUser();
        String location = createResource("/users", newUser);
        UserResponseDTO retrievedUser = getResource(location, UserResponseDTO.class);
        assertEqualUser(newUser, retrievedUser);
    }

    private UserRequestDTO createDummyUser(){
        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setFirst_name("Teste");
        userRequestDTO.setLast_name("Teste");
        userRequestDTO.setEmail("i@t.co");
        userRequestDTO.setGender("female");
        userRequestDTO.setPhone("999999999");
        userRequestDTO.setStatus("active");
        return userRequestDTO;
    }

    private String createResource(String path, Object bodyPayload){
        return given()
                .spec(spec)
                .body(bodyPayload)
                .when()
                .post(path)
                .then()
                .statusCode(302)
                .extract().header("Location");
    }

    private <T> T getResource(String location, Class<T> responseClass){
        return given()
                .spec(spec)
                .when()
                .get(location)
                .then()
                .statusCode(200)
                .extract().as(responseClass);
    }

    private void assertEqualUser(UserRequestDTO newUser, UserResponseDTO retrievedUser){
        assertThat(retrievedUser.result).isEqualToIgnoringGivenFields(newUser, "id");
    }
}
