package helpers;

import java.sql.*;

public class DataBaseHelper {

    /**
     * Параметры подключения к базе данных берутся из файла настроек через класс ParametersProvider.
     */
    private static final String DB_URL = ParametersProvider.getProperty("urlDB");
    private static final String DB_USERNAME = ParametersProvider.getProperty("usernameDB");
    private static final String DB_PASSWORD = ParametersProvider.getProperty("passwordDB");

    /**
     * Внутренний метод для выполнения SQL-запросов на обновление (INSERT, UPDATE, DELETE).
     * Открывает соединение с базой данных, готовит и выполняет SQL-запрос, освобождая ресурсы после выполнения.
     *
     * @param sql    SQL-запрос на обновление
     * @param params Массив параметров для заполнения в запросе
     */
    static void executeUpdate(String sql, Object... params) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD); // Получаем соединение из пула
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Внутренний метод для выполнения SQL-запросов на выборку данных (SELECT).
     * Открывает соединение с базой данных, готовит и выполняет SQL-запрос, преобразуя результат в указанный тип данных.
     *
     * @param sql       SQL-запрос на выборку
     * @param rowMapper Интерфейс RowMapper для преобразования строки ResultSet в объект
     * @param params    Массив параметров для заполнения в запросе
     * @param <T>       Тип возвращаемых данных
     * @return Результат выполнения запроса
     */
    static <T> T executeQuery(String sql, RowMapper<T> rowMapper, Object... params) {
        T result = null;
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                result = rowMapper.map(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Интерфейс для преобразования строк ResultSet в объекты.
     *
     * @param <T> Тип объекта, в который преобразуются данные
     */
    @FunctionalInterface
    public interface RowMapper<T> {
        T map(ResultSet rs) throws SQLException;
    }
}