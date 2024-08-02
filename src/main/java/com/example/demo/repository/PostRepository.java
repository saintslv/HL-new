package com.example.demo.repository;

import com.example.demo.model.Post;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class PostRepository {
    private final DataSource dataSource;

    public PostRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Post> getFeed(int offset, int limit, UUID userId) {
        List<Post> feed = new ArrayList<>();

        // SQL query to get posts from friends of the user
        String sql =
                "SELECT p.id, p.user_id, p.text, p.created_at " +
                        "FROM user_post p " +
                        "JOIN user_friend uf ON p.user_id = uf.friend_id " +
                        "WHERE uf.user_id = ? " +
                        "ORDER BY p.created_at DESC " +
                        "LIMIT ? OFFSET ?";

        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setObject(1, userId); // Set the user ID
                statement.setInt(2, limit); // Set the limit
                statement.setInt(3, offset); // Set the offset

                try (ResultSet rs = statement.executeQuery()) {
                    while (rs.next()) {
                        UUID postId = (UUID) rs.getObject("id");
                        UUID postUserId = (UUID) rs.getObject("user_id");
                        String text = rs.getString("text");
                        Timestamp dateTimestamp = rs.getTimestamp("created_at");
                        LocalDateTime date = dateTimestamp.toLocalDateTime();
                        feed.add(new Post(postId, postUserId, text, date));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return feed;
    }
}
