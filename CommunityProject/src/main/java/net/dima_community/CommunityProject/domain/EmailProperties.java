package net.dima_community.CommunityProject.domain;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "spring.mail")
public class EmailProperties {
    private String host;
    private String username;
    private String password;
    private int port;
}
//이메일 보낼 사람의 정보