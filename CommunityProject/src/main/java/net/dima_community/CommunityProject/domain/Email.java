package net.dima_community.CommunityProject.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
//보낼 이메일에 어떤걸 저장할건지. 
public class Email {
    public String to;
    public String from;
    public String title;
    public String content;
};