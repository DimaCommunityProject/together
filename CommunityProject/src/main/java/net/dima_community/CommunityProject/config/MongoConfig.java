package net.dima_community.CommunityProject.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;
import net.dima_community.CommunityProject.repository.chat.ChatMessageRepository;

@Slf4j
@Configuration
public class MongoConfig {

	@Bean
	public CommandLineRunner checkMongoConnection(ChatMessageRepository repository) {
	    return args -> {
	        log.info("CommandLineRunner 실행");
	        if (repository.count() >= 0) {
	            log.info("MongoDB 연결 성공");
	        } else {
	            log.error("MongoDB 연결 실패");
	        }
	    };
	}
	
	
}