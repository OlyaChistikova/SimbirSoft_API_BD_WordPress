package tests;

import org.testng.Assert;
import org.testng.annotations.*;
import pojo.DataUser;
import pojo.User;

import java.util.List;
import java.util.stream.Collectors;

import static helpers.BaseRequests.*;
import static helpers.UserRepository.*;

public class GetUserTest extends BaseTest {
    private Integer userId;

    @BeforeClass
    public void createUserDB() {
        DataUser requestPost = createUserBody(usernameTest, emailTest, passwordTest);
        addUser(requestPost);
        userId = getUserByName(requestPost.getUsername()).getId();
    }

    @AfterClass
    public void deleteUserDB() {
        deleteUser(userId);
    }

    @Test
    public void getAllUsersDBTest() {
        List<User> listUsers = getResourceAsList(User.class, USERS_PATH, TOKEN);
        List<Integer> ids = listUsers.stream().map(User::getId).collect(Collectors.toList());

        Assert.assertTrue(listUsers.size() > 1);
        Assert.assertTrue(ids.contains(userId));
    }

    @Test
    public void getUserDBTest() {
        User responseUser = getItemById(User.class, USERS_PATH, userId, TOKEN);

        //Проверяет, что пользователь существует в базе и его параметры совпадают.
        Assert.assertEquals(getUserById(userId).getId(), userId, "ID пользователя в базе не совпадает");
        Assert.assertEquals(getUserById(userId).getUsername(), responseUser.getName(), "Имя пользователя в базе не совпадает");
    }
}