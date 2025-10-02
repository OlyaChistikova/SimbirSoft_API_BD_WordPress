package tests;

import helpers.DataBaseHelper;
import org.testng.Assert;
import org.testng.annotations.Test;
import pojo.DataError;
import pojo.DataPost;

import java.util.List;
import java.util.stream.Collectors;

import static helpers.BaseRequests.*;

public class CreatePostTest extends BaseTest {

    @Test
    public void createPostWithCorrectDataTest() {
        DataPost requestPost = createPostBody("Тестовый заголовок", "Привет! Это мой тестовый пост.", "publish");
        DataPost responsePost = addPostSuccessRequest(requestPost, TOKEN);

        Integer postId = responsePost.getId();
        Assert.assertEquals(requestPost.getTitle().getRaw(), responsePost.getTitle().getRaw());
        Assert.assertEquals(requestPost.getStatus(), responsePost.getStatus());

        List<DataPost> listPosts = getResourceAsList(DataPost.class, POSTS_PATH, TOKEN);
        List<Integer> ids = listPosts.stream().map(DataPost::getId).collect(Collectors.toList());
        List<String> titles = listPosts.stream()
                .map(post -> post.getTitle().getRendered())
                .collect(Collectors.toList());
        Assert.assertTrue(ids.contains(postId));
        Assert.assertTrue(titles.contains(responsePost.getTitle().getRendered()));

        //Проверяет, что пост существует в базе и его параметры совпадают.
        Assert.assertEquals(DataBaseHelper.getPostById(postId).getId(), postId, "ID поста в базе не совпадает");
        Assert.assertEquals(DataBaseHelper.getPostById(postId).getTitle().getRaw(), requestPost.getTitle().getRaw(), "Заголовок поста в базе не совпадает");
        Assert.assertEquals(DataBaseHelper.getPostById(postId).getContent().getRaw(), requestPost.getContent().getRaw(), "Содержимое поста в базе не совпадает");
        Assert.assertEquals(DataBaseHelper.getPostById(postId).getStatus(), requestPost.getStatus(), "Статус поста в базе не совпадает");

        deleteItemById(POSTS_PATH, postId, TOKEN);

        //Проверяем в базе данных, что пост поменял статус на удаленный
        Assert.assertEquals(DataBaseHelper.getPostById(postId).getId(), postId, "Пост не найден в базе");
        Assert.assertEquals(DataBaseHelper.getPostById(postId).getStatus(), "trash", "Статус поста в базе не совпадает");
    }

    @Test
    public void createPostWithMinimalDataTest() {
        DataPost requestPost = createPostBody("Тестовый заголовок", null, null);
        DataPost responsePost = addPostSuccessRequest(requestPost, TOKEN);

        Integer postId = responsePost.getId();
        Assert.assertEquals(requestPost.getTitle().getRendered(), responsePost.getTitle().getRendered());
        Assert.assertTrue(responsePost.getContent().getRendered().isEmpty());
        Assert.assertEquals(responsePost.getStatus(), "draft");

        DataPost postById = getItemById(DataPost.class, POSTS_PATH, postId, TOKEN);
        Assert.assertEquals(postById.getTitle().getRendered(), responsePost.getTitle().getRendered());

        //Проверяет, что пост существует в базе и его параметры совпадают.
        Assert.assertEquals(DataBaseHelper.getPostById(postId).getId(), postId, "ID поста в базе не совпадает");
        Assert.assertEquals(DataBaseHelper.getPostById(postId).getTitle().getRaw(), responsePost.getTitle().getRaw(), "Заголовок поста в базе не совпадает");
        Assert.assertEquals(DataBaseHelper.getPostById(postId).getContent().getRaw(), responsePost.getContent().getRaw(), "Содержимое поста в базе не совпадает");
        Assert.assertEquals(DataBaseHelper.getPostById(postId).getStatus(), responsePost.getStatus(), "Статус поста в базе не совпадает");

        deleteItemById(POSTS_PATH, postId, TOKEN);

        //Проверяем в базе данных, что пост поменял статус на удаленный
        Assert.assertEquals(DataBaseHelper.getPostById(postId).getId(), postId, "Пост не найден в базе");
        Assert.assertEquals(DataBaseHelper.getPostById(postId).getStatus(), "trash", "Статус поста в базе не совпадает");
    }

    @Test
    public void createPostWithInvalidDataTest() {
        DataPost requestPost = createPostBody(null, null, "publish");
        DataError responsePost = addPostInvalidRequest(requestPost);

        Assert.assertEquals(responsePost.getCode(), "empty_content");
        Assert.assertEquals(responsePost.getMessage(), "Содержимое, заголовок и отрывок пусты.");
        Assert.assertEquals(responsePost.getData().getStatus(), 400);
    }

    @Test
    public void createCorrectPostWithoutAuthTest() {
        DataPost requestPost = createPostBody("Тестовый заголовок без авторизации", "Привет! Это мой тестовый пост без авторизации.", "publish");
        DataError responsePost = addPostWithoutAuth(requestPost);

        Assert.assertEquals(responsePost.getCode(), "rest_cannot_create");
        Assert.assertEquals(responsePost.getMessage(), "Извините, вам не разрешено создавать записи от лица этого пользователя.");
        Assert.assertEquals(responsePost.getData().getStatus(), 401);

        List<DataPost> listPosts = getResourceAsList(DataPost.class, POSTS_PATH, TOKEN);
        List<String> titles = listPosts.stream()
                .map(post -> post.getTitle().getRendered())
                .collect(Collectors.toList());
        List<String> contents = listPosts.stream()
                .map(post -> post.getContent().getRendered())
                .collect(Collectors.toList());
        Assert.assertFalse(titles.contains(requestPost.getTitle().getRendered()));
        Assert.assertFalse(contents.contains(requestPost.getContent().getRendered()));
    }
}