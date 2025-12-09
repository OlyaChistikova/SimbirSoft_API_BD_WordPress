package helpers;

import pojo.DataUser;

import static helpers.DataBaseHelper.*;

public class UserRepository {

    /**
     * Метод для добавления нового пользователя в базу данных.
     *
     * @param user Экземпляр класса DataUser, содержащий данные пользователя
     */
    public static void addUser(DataUser user) {
        String sqlUser = "INSERT INTO wp_users (user_login, user_pass, user_nicename, user_email, user_url, user_registered, user_activation_key, user_status, display_name) VALUES(?, ?, '', ?, '', NOW(), '', 0, ?)";
        executeUpdate(sqlUser, user.getUsername(), user.getPassword(), user.getEmail(), user.getUsername());
    }

    /**
     * Метод для получения данных пользователя по его id.
     *
     * @param id Уникальный логин пользователя
     * @return Объект DataUser, соответствующий запрошенному посту, или null, если пользователь не найден
     */
    public static DataUser getUserById(Integer id) {
        String sql = "SELECT ID, user_login, user_email, user_pass FROM wp_users WHERE ID = ?;";
        return executeQuery(sql, rs -> {
            if (rs.next()) {
                return new DataUser(rs.getInt("ID"), rs.getString("user_login"), rs.getString("user_email"), rs.getString("user_pass"));
            }
            return null;
        }, id);
    }

    /**
     * Метод для получения данных пользователя по его username.
     *
     * @param username Уникальный логин пользователя
     * @return Объект DataUser, соответствующий запрошенному посту, или null, если пользователь не найден
     */
    public static DataUser getUserByName(String username) {
        String sql = "SELECT ID, user_login, user_email, user_pass FROM wp_users WHERE user_login = ?;";
        return executeQuery(sql, rs -> {
            if (rs.next()) {
                return new DataUser(rs.getInt("ID"), rs.getString("user_login"), rs.getString("user_email"), rs.getString("user_pass"));
            }
            return null;
        }, username);
    }

    /**
     * Метод для удаления пользователя по его ID.
     *
     * @param id Уникальный идентификатор пользователя для удаления
     */
    public static void deleteUser(Integer id) {
        String sql = "DELETE FROM wp_users WHERE ID = ?";
        executeUpdate(sql, id);
    }
}
