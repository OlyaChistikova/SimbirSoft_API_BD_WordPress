package tests;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pojo.DataError;
import pojo.DataPost;

import java.util.List;
import java.util.stream.Collectors;

import static helpers.BaseRequests.*;
import static helpers.PostRepository.*;

public class GetPostTest extends BaseTest {
    private Integer publicPostId;
    private Integer privatePostId;

    @BeforeClass
    public void createPostDB() {
        // Создаем публичный пост
        DataPost requestPublicPost = createPostBody("Созданная публичная запись в бд", "Контент созданной записи", "publish");
        addPost(requestPublicPost, 1);
        publicPostId = getPostByTitle(requestPublicPost.getTitle().getRaw()).getId();
        // Создаем приватный пост
        DataPost requestPrivatePost = createPostBody("Созданная приватная запись в бд", "Контент созданной записи", "private");
        addPost(requestPrivatePost, 1);
        privatePostId = getPostByTitle(requestPrivatePost.getTitle().getRaw()).getId();
    }

    @AfterClass
    public void deletePostDB() {
        deletePost(publicPostId);
        deletePost(privatePostId);
    }

    @Test
    public void getAllPostsDBTest() {
        List<DataPost> listPosts = getResourceAsList(DataPost.class, POSTS_PATH, TOKEN);
        List<Integer> ids = listPosts.stream().map(DataPost::getId).collect(Collectors.toList());

        List<DataPost> dbListPosts = getAllPosts();
        List<Integer> idsDb = dbListPosts.stream().map(DataPost::getId).collect(Collectors.toList());

        Assert.assertTrue(ids.contains(publicPostId));
        Assert.assertTrue(idsDb.containsAll(ids), "Не все ожидаемые IDs из API найдены в списке из базы данных");
    }

    @Test
    public void getPublicPostDBTest() {
        DataPost responsePost = getItemById(DataPost.class, POSTS_PATH, publicPostId, TOKEN);
        String contentResponse = responsePost.getContent().getRendered().replace("<p>", "").replace("</p>", "").trim();

        checkSuccessPostDb(publicPostId, responsePost.getTitle().getRendered(), contentResponse, responsePost.getStatus());
    }

    @Test
    public void getPrivatePostDBInvalidAuthTest() {
        String authOtherHeader = createBasicAuthHeader(usernameAuthor, passwordAuthor);
        DataError responseError = getPostByIdWithInvalidAuth(privatePostId, authOtherHeader);

        Assert.assertEquals(responseError.getCode(), "rest_forbidden");
        Assert.assertEquals(responseError.getMessage(), "Извините, вам не разрешено выполнять данное действие.");
    }

    @Test
    public void getPrivatePostDBWithoutAuthTest() {
        DataError responseError = getPostByIdWithoutAuth(privatePostId);

        Assert.assertEquals(responseError.getCode(), "rest_forbidden");
        Assert.assertEquals(responseError.getMessage(), "Извините, вам не разрешено выполнять данное действие.");
    }
}