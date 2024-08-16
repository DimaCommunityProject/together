package net.dima_community.CommunityProject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import net.dima_community.CommunityProject.repository.jpa.ChatRoomRepository;
import net.dima_community.CommunityProject.repository.jpa.ChattingRoomMemberRepository;
import net.dima_community.CommunityProject.repository.mongo.ChatMessageRepository;

@SpringBootApplication
@EnableMongoRepositories(basePackages = "net.dima_community.CommunityProject.repository.mongo")
@EnableJpaRepositories(basePackages = "net.dima_community.CommunityProject.repository.jpa")

public class CommunityProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(CommunityProjectApplication.class, args);
	}

}
