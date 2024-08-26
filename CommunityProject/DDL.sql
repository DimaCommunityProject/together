# database 전환
USE dima;

# 기존 table 삭제

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS member;
DROP TABLE IF EXISTS chat_rooms;
DROP TABLE IF EXISTS chatting_room_member;
SET FOREIGN_KEY_CHECKS = 1;

-- 1. Member 테이블 생성
CREATE TABLE member (
    member_id VARCHAR(255) NOT NULL PRIMARY KEY,
    member_pw VARCHAR(255) NOT NULL,
    member_enabled BOOLEAN NOT NULL,
    member_role VARCHAR(255) NOT NULL,
    member_name VARCHAR(255) NOT NULL,
    member_email VARCHAR(255) NOT NULL,
    member_group VARCHAR(255) NOT NULL,
    member_phone VARCHAR(255) NOT NULL,
    badge1 VARCHAR(255),
    badge2 VARCHAR(255),
    member_git VARCHAR(255),
    member_blog VARCHAR(255),
    member_resume VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. Chat_rooms 테이블 생성
CREATE TABLE chat_rooms (
    chatting_room_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    created_date DATETIME(6),
    deleted BIT NOT NULL,
    deleted_date DATETIME(6),
    last_modified_date DATETIME(6),
    name VARCHAR(255) NOT NULL
) ENGINE=InnoDB;

-- 3. Chatting_room_member 테이블 생성
CREATE TABLE chatting_room_member (
    chatting_room_member_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    created_date DATETIME(6),
    deleted BIT NOT NULL,
    deleted_date DATETIME(6),
    last_modified_date DATETIME(6),
	chatting_room_id BIGINT,
    member_id VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    FOREIGN KEY (chatting_room_id) REFERENCES chat_rooms (chatting_room_id),
    FOREIGN KEY (member_id) REFERENCES member (member_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. 제약 조건

ALTER TABLE chatting_room_member
ADD CONSTRAINT utf8mb4_unicode_ci
FOREIGN KEY(member_id) REFERENCES member(member_id);

-- CHARSET과 COLLATION 확인 
SHOW FUll COLUMNS FROM member WHERE Field = 'member_id';
SHOW FULL COLUMNS FROM chatting_room_member WHERE Field = 'member_id';


# data 조회
select * from member;
select * from chat_rooms;
select * from chatting_room_member;

SELECT * FROM chatting_room_member WHERE member_id = 'aaaaa';

# 테이블명 변경
rename table chatting_room to chat_rooms;

describe member;
describe member_chatting_room;
