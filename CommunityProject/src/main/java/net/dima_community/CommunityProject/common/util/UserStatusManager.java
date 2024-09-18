package net.dima_community.CommunityProject.common.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class UserStatusManager {

    // 사용자 상태를 저장하는 맵 (key: 사용자 ID, value: online/offline 상태)
	private static Map<String, String> userStatusMap = new ConcurrentHashMap<>();

    // 사용자 상태 업데이트 (입장 시 online, 퇴장 시 offline)
	public static void updateUserStatus(String userId, String status) {
        userStatusMap.put(userId, status);
    }

    // 특정 사용자 상태 조회
	public static String getUserStatus(String userId) {
        return userStatusMap.getOrDefault(userId, "offline");  // 기본값은 offline
    }

    // 사용자 상태 제거 (예: 로그아웃 등)
    public static void removeUser(String userId) {
        userStatusMap.remove(userId);
    }
}
