package net.dima_community.CommunityProject.common.exception;

public class ProcessException extends RuntimeException {

    public ProcessException(String datasource) {
        super(datasource + "실행을 실패하였습니다.");
    }

}
