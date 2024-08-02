package com.example.demo.config;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
public class DataSourceConfig {

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl(System.getenv("DATABASE_URL"));
        dataSource.setUsername(System.getenv("DATABASE_USER"));
        dataSource.setPassword(System.getenv("DATABASE_PASSWORD"));
        return dataSource;
    }


    @Bean
    public RedisClient redisClient() {
        String redisUrl = System.getenv("REDIS_URL");
        return RedisClient.create(redisUrl);
    }

    @Bean
    public StatefulRedisConnection<String, String> redisConnection(RedisClient redisClient) {
        return redisClient.connect();
    }

    @Bean
    public RedisCommands<String, String> redisCommands(StatefulRedisConnection<String, String> connection) {
        return connection.sync();
    }
//@Bean
//    public DataSource dataSource() {
//        DriverManagerDataSource dataSource = new DriverManagerDataSource();
//        dataSource.setDriverClassName("org.postgresql.Driver");
//        dataSource.setUrl("jdbc:postgresql://localhost:5432/user");
//        dataSource.setUsername("postgres");
//        dataSource.setPassword("postgres");
//        return dataSource;
//    }
}
