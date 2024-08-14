package net.dima_community.CommunityProject.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import lombok.extern.slf4j.Slf4j;
import net.dima_community.CommunityProject.entity.ChatMessage;
import net.dima_community.CommunityProject.repository.mongo.ChatMessageRepository;

@Slf4j
@Configuration
public class MongoConfig {

    private final MongoTemplate mongoTemplate;

    public MongoConfig(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Bean
    public CommandLineRunner checkMongoConnection(ChatMessageRepository repository) {
        return args -> {
            log.info("CommandLineRunner 실행");

            if (repository.count() >= 0) {
                log.info("MongoDB 연결 성공");

                // 테스트 메시지 저장
                mongoTemplate.save(new ChatMessage("testRoomId", "testMessage"));
                log.info("Test message saved to MongoDB");
            } else {
                log.error("MongoDB 연결 실패");
            }
        };
    }
}