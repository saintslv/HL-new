package com.example.demo.test;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.*;

@RestController
@RequestMapping("/test")
public class ReplicationInsert {

    private final DataSource dataSource;

    public ReplicationInsert(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostMapping("/insert")
    public Long insert() {
        String insertTestSql = "INSERT INTO test (id) VALUES (DEFAULT)";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(insertTestSql, Statement.RETURN_GENERATED_KEYS)) {

            statement.executeUpdate();

            // Get the generated keys
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                } else {
                    throw new SQLException("Creating entry failed, no ID obtained.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
