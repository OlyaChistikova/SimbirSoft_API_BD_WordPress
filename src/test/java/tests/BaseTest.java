package tests;

import helpers.BaseRequests;
import helpers.ParametersProvider;
import io.restassured.response.ResponseBodyExtractionOptions;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class BaseTest {

    /**
     * Токен авторизации для доступа к API.
     */
    protected static final String TOKEN = BaseRequests.TOKEN;

    /**
     * Путь для взаимодействия с постами.
     */
    public static final String POSTS_PATH = BaseRequests.POSTS_PATH;

    /**
     * Путь для взаимодействия с пользователями.
     */
    public static final String USERS_PATH = BaseRequests.USERS_PATH;

    /**
     * Данные для авторизации
     */
    protected static final String usernameAdmin = ParametersProvider.getProperty("usernameAdmin");
    protected static final String passwordAdmin = ParametersProvider.getProperty("passwordAdmin");
    protected static final String usernameAuthor = ParametersProvider.getProperty("usernameAuthor");
    protected static final String passwordAuthor = ParametersProvider.getProperty("passwordAuthor");
    protected static final String usernameTest = ParametersProvider.getProperty("usernameTest");
    protected static final String emailTest = ParametersProvider.getProperty("emailTest");
    protected static final String passwordTest = ParametersProvider.getProperty("passwordTest");

    /**
     * Получение списка объектов любого типа по указанному маршруту без авторизации.
     *
     * @param tClass       Тип объекта, который нужно вернуть.
     * @param resourcePath Маршрут к API-ресурсу.
     * @param <T>          Тип объектов в списке.
     * @return Список объектов заданного типа.
     */
    public <T> List<T> getResourceAsList(Class<T> tClass, String resourcePath, String authToken) {
        ResponseBodyExtractionOptions body = given()
                .spec(BaseRequests.requestSpec(authToken))
                .when()
                .get(resourcePath)
                .then()
                .statusCode(200)
                .extract().body();

        return body.jsonPath().getList("", tClass);
    }

    /**
     * Получает объект по ID с авторизацией.
     *
     * @param tClass       Тип объекта, который нужно вернуть.
     * @param resourcePath Маршрут к API-ресурсу.
     * @param itemId       Идентификатор нужного объекта.
     * @param authToken    Токен аутентификации.
     * @param <T>          Тип объекта.
     * @return Объект заданного типа.
     */
    public <T> T getItemById(Class<T> tClass, String resourcePath, Integer itemId, String authToken) {
        ResponseBodyExtractionOptions body = given()
                .spec(BaseRequests.requestSpec(authToken))
                .when()
                .get(resourcePath + "/" + itemId)
                .then()
                .statusCode(200)
                .body("id", equalTo(itemId))
                .extract().body();

        return body.as(tClass);
    }

    /**
     * Удаляет объект по его ID с авторизацией.
     *
     * @param resourcePath Маршрут к API-ресурсу.
     * @param itemId       Идентификатор объекта для удаления.
     * @param authToken    Токен аутентификации.
     */
    public void deleteItemById(String resourcePath, Integer itemId, String authToken) {
        given()
                .spec(BaseRequests.requestSpec(authToken))
                .when()
                .delete(resourcePath + "/" + itemId)
                .then()
                .statusCode(200);
    }
}