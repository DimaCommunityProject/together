package net.dima_community.CommunityProject.common.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String datasource, String id) {
        super(datasource + "안에서" + id + "에 해당하는 정보를 찾을 수 없습니다");
    }
}
