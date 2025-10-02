package helpers;

import pojo.DataPost;

import java.util.ArrayList;
import java.util.List;

import static helpers.DataBaseHelper.*;

public class PostRepository {

    /**
     * Метод для добавления нового поста в базу данных.
     *
     * @param post   Экземпляр класса DataPost, содержащий данные поста
     * @param author ID автора поста
     */
    public static void addPost(DataPost post, int author) {
        String sql = "INSERT INTO wp_posts (post_author, post_date, post_date_gmt, post_content, post_title, post_excerpt, post_status, comment_status, ping_status, post_password, post_name, to_ping, pinged, post_modified, post_modified_gmt, post_content_filtered, post_parent, guid, menu_order, post_type, post_mime_type, comment_count) VALUES (?, NOW(), NOW(), ?, ?, '', ?, 'open', 'open', '', '', '', '', NOW(), NOW(), '', 0, '', 0, 'post', '', 0)";
        executeUpdate(sql, author, post.getContent().getRendered(), post.getTitle().getRaw(), post.getStatus());
    }

    /**
     * Метод для получения всех постов из базы данных.
     *
     * @return Список объектов DataPost, содержащих все доступные посты
     */
    public static List<DataPost> getAllPosts() {
        String sql = "SELECT ID, post_title, post_content, post_status FROM wp_posts";
        return DataBaseHelper.executeQuery(sql, rs -> {
            List<DataPost> posts = new ArrayList<>();
            while (rs.next()) {
                DataPost.Title titleObj = new DataPost.Title(rs.getString("post_title"), "");
                DataPost.Content contentObj = new DataPost.Content(rs.getString("post_content"), "");
                posts.add(new DataPost(rs.getInt("ID"), titleObj, contentObj, rs.getString("post_status")));
            }
            return posts;
        });
    }

    /**
     * Метод для получения поста по его ID.
     *
     * @param id Уникальный идентификатор поста
     * @return Объект DataPost, соответствующий запрошенному посту, или null, если пост не найден
     */
    public static DataPost getPostById(int id) {
        String sql = "SELECT ID, post_title, post_content, post_status FROM wp_posts WHERE ID = ?";
        return DataBaseHelper.executeQuery(sql, rs -> {
            if (rs.next()) {
                DataPost.Title titleObj = new DataPost.Title(rs.getString("post_title"), "");
                DataPost.Content contentObj = new DataPost.Content(rs.getString("post_content"), "");
                return new DataPost(rs.getInt("ID"), titleObj, contentObj, rs.getString("post_status"));
            }
            return null;
        }, id);
    }

    /**
     * Метод для получения ID поста по его title.
     *
     * @param title Уникальный заголовок поста
     * @return Объект DataPost, соответствующий запрошенному посту, или null, если пост не найден
     */
    public static DataPost getPostByTitle(String title) {
        String sql = "SELECT ID, post_title, post_content, post_status FROM wp_posts WHERE post_title = ?";
        return DataBaseHelper.executeQuery(sql, rs -> {
            if (rs.next()) {
                DataPost.Title titleObj = new DataPost.Title(rs.getString("post_title"), "");
                DataPost.Content contentObj = new DataPost.Content(rs.getString("post_content"), "");
                return new DataPost(rs.getInt("ID"), titleObj, contentObj, rs.getString("post_status"));
            }
            return null;
        }, title);
    }

    /**
     * Метод для удаления поста по его ID.
     *
     * @param id Уникальный идентификатор поста для удаления
     */
    public static void deletePost(Integer id) {
        String sql = "DELETE FROM wp_posts WHERE ID = ?";
        executeUpdate(sql, id);
    }
}
