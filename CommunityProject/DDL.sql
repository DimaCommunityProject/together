# database 전환
USE dima;

# 기존 table 삭제

SET FOREIGN_KEY_CHECKS = 0;

-- member data 삭제 
DROP TABLE IF EXISTS member;
drop table if exists member_verify_code;
drop table if exists memberpage;
drop table if exists memberproject;

-- reply data 삭제
drop table if exists reply;


-- chat data 삭제 
DROP TABLE IF EXISTS chat_rooms;
DROP TABLE IF EXISTS chatting_room_member;

SET FOREIGN_KEY_CHECKS = 1;

-- 1. Member 테이블 생성
CREATE TABLE member (
	id BIGINT NOT NULL AUTO_INCREMENT,    
    member_id VARCHAR(255) NOT NULL UNIQUE,
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
    member_resume VARCHAR(255),
    PRIMARY KEY (id)
) ;

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
    deleted  int nOT NULL,
    deleted_date DATETIME(6),
    last_modified_date DATETIME(6),
	chatting_room_id BIGINT,
    member_id VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    FOREIGN KEY (chatting_room_id) REFERENCES chat_rooms (chatting_room_id),
    FOREIGN KEY (member_id) REFERENCES member (member_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 4. 제약 조건

ALTER TABLE chatting_room_member
ADD CONSTRAINT utf8mb4_unicode_ci
FOREIGN KEY(member_id) REFERENCES member(member_id);

-- 5. 시퀀스 번호 생성
ALTER TABLE member ADD COLUMN id INT;
SET SQL_SAFE_UPDATES= 1;
UPDATE member SET id = 0;
SET @counter = 0;
UPDATE member SET id = (@counter := @counter + 1) ;

ALTER TABLE chatting_room_member DROP FOREIGN KEY fk_member_id;
alter table member drop primary key;

-- CHARSET과 COLLATION 확인 
SHOW FUll COLUMNS FROM member WHERE Field = 'member_id';
SHOW FULL COLUMNS FROM chatting_room_member WHERE Field = 'member_id';


SHOW CREATE TABLE chatting_room_member;

# member data 조회
select * from member;
select * from memberpage;
select * from member_verify_code;
select * from memberproject;
select * from adminnote;
select * from user_status;
select * from reply;

# board data 조회
select * from reply;
select * from board_report;
select * from board;


# chat data 조회
select * from chat_rooms; 
select * from chatting_room_member;

# 특정 값 추가 
DELETE FROM member WHERE member_id = 'admin';
UPDATE member
SET member_role = 'ROLE_ADMIN'
WHERE member_id = 'admin123';

# 특정 값 조회 
SELECT * FROM chatting_room_member WHERE member_id = 'aaaaa';
SELECT * FROM memberpage WHERE member_id = 'inyoung123';
ALTER TABLE MEMBER_VERIFY_CODE ADD CONSTRAINT UNIQUE(member_id);

# 테이블명 변경
rename table chatting_room to chat_rooms;

describe member;
describe chatting_room_member;
