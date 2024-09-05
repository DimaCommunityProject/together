package net.dima_community.CommunityProject.email.domain;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class Email {
    public String to;
    public String from;
    public String title;
    public String content;

};
