use dimacommunity;

-- 취업 게시판 Table

DROP TABLE IF EXISTS job_board;

CREATE TABLE job_board (
    job_board_id bigint NOT NULL AUTO_INCREMENT,
    deadline TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    limit_number INT DEFAULT 0,
    current_number INT DEFAULT 0,
    PRIMARY KEY (job_board_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

SELECT * FROM job_board;




-- 취업 게시판 구인 Table

DROP TABLE IF EXISTS job_board_recruit;

CREATE TABLE job_board_recruit (
    recruit_id BIGINT NOT NULL AUTO_INCREMENT,
    job_board_id BIGINT NOT NULL,
    member_id VARCHAR(30) NOT NULL,
    member_group VARCHAR(20) NOT NULL,
    member_phone VARCHAR(20),
    member_email VARCHAR(200),
    PRIMARY KEY (recruit_id),
    FOREIGN KEY (job_board_id) REFERENCES job_board(job_board_id), -- job_board 테이블의 job_board_id를 FK로 참조
    FOREIGN KEY (member_id) REFERENCES member(member_id) -- member 테이블의 member_id를 FK로 참조
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

SELECT * FROM job_board_recruit;




-- 게시글 Table

DROP TABLE IF EXISTS board;

CREATE TABLE board (
    board_id BIGINT NOT NULL AUTO_INCREMENT,
    member_id VARCHAR(30) NOT NULL,
    member_group VARCHAR(20) NOT NULL,
    category VARCHAR(20) NOT NULL CHECK (category IN ('code', 'project', 'activity', 'recruit', 'free', 'group','info')),
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    create_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_date TIMESTAMP,
    hit_count INT DEFAULT 0,
    like_count INT DEFAULT 0,
    original_file_name VARCHAR(200),
    saved_file_name VARCHAR(200),
    reported INT DEFAULT 0 CHECK(reported IN (0,1)),
	job_board_id bigint,
    PRIMARY KEY (board_id),
    FOREIGN KEY (member_id) REFERENCES member(member_id), -- member 테이블의 member_id를 FK로 참조
    FOREIGN KEY (job_board_id) REFERENCES job_board(job_board_id) -- job_board 테이블의 job_board_id를 FK로 참조
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- replyCount (댓글 수) 컬럼 추가
ALTER TABLE board
ADD COLUMN reply_count int DEFAULT 0;

SELECT * FROM board;



-- 게시글 신고 Table

DROP TABLE IF EXISTS board_report;

CREATE TABLE board_report(
    report_id BIGINT NOT NULL AUTO_INCREMENT,
    board_id BIGINT NOT NULL,
    member_id VARCHAR(30) NOT NULL, -- 신고 당한 게시글 작성자 ID
    category VARCHAR(100) NOT NULL CHECK (category IN ( 'IllegalContent', 'ViolentContent', 'PrivacyViolation', 'Spam', 'ETC')),
    reason TEXT,
    report_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (report_id),
    FOREIGN KEY (board_id) REFERENCES board(board_id) -- board 테이블의 board_id를 FK로 참조
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

SELECT * FROM board_report;




-- 댓글 Table

DROP TABLE IF EXISTS reply;

CREATE TABLE reply (
    reply_id BIGINT NOT NULL AUTO_INCREMENT,
    board_id BIGINT NOT NULL,
    parent_reply_id BIGINT,
    member_id VARCHAR(30) NOT NULL,
    content VARCHAR(200) NOT NULL,
    create_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    like_count INT DEFAULT 0,
    PRIMARY KEY (reply_id),
    FOREIGN KEY (board_id) REFERENCES board(board_id), -- board 테이블의 board_id를 FK로 참조
    FOREIGN KEY (member_id) REFERENCES member(member_id) -- member 테이블의 member_id를 FK로 참조
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

SELECT * FROM reply;



-- 좋아요 Table

DROP TABLE IF EXISTS likes;

CREATE TABLE likes (
    like_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id VARCHAR(30) NOT NULL,
    board_id BIGINT,
    reply_id BIGINT,
    FOREIGN KEY (member_id) REFERENCES member(member_id),
    FOREIGN KEY (board_id) REFERENCES board(board_id),
    FOREIGN KEY (reply_id) REFERENCES reply(reply_id),
    CHECK (
        (board_id IS NOT NULL AND reply_id IS NULL) OR
        (board_id IS NULL AND reply_id IS NOT NULL)
    ) -- board_id 나 reply_id는 둘 중에 하나는 무조건 null이어야 함
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

SELECT * FROM likes;

commit;

