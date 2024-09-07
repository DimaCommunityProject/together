package net.dima_community.CommunityProject.common.util;

import java.io.File;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

public class FileService {

	public static String saveFile(MultipartFile uploadFile, String uploadPath) { // uploadPath : application에서 설정했음

		// 파일이 첨부되면 디렉토리(폴더)가 있는지 확인
		// 없으면 생성, 있으면 그대로 사용
		if (!uploadFile.isEmpty()) {
			File path = new File(uploadPath);
			if (!path.isDirectory()) {
				path.mkdirs();
			}
		} // end if

		// 원본파일명 추출
		String originalFileName = uploadFile.getOriginalFilename();

		// 랜덤값 발생
		String uuid = UUID.randomUUID().toString();

		// 원본에서 파일명과 확장자 분리작업(.을 기준으로 분리)
		String filename;
		String ext;
		String savedFileName;

		// . 위치 찾음. 없으면 -1 반환
		int position = originalFileName.lastIndexOf(".");

		// 확장자가 없는 경우
		if (position == -1)
			ext = "";

		// 확장자가 있는 경우
		else
			ext = "." + originalFileName.substring(position + 1); // . 뒤의 모든것

		filename = originalFileName.substring(0, position); // 맨 앞부터 . 앞까지
		savedFileName = filename + "_" + uuid + ext; // savedFileName : bts_asds.jpg

		// 3) 서버의 저장공간에 파일 저장
		File serverFile = null;
		serverFile = new File(uploadPath + "/" + savedFileName); // uploadPath/bts_asds.jpg

		try {
			uploadFile.transferTo(serverFile);
			// uploadFile을 서버에 저장할 파일로 전송.
		} catch (Exception e) {
			savedFileName = null;
			e.printStackTrace();
		}
		return savedFileName; // 저장 파일명 반환
	}// end saveFile

	// 저장장치에 저장된 파일을 삭제(경로 + 파일명)
	public static boolean deleteFile(String fullPath) {
		boolean result = false;

		File delFile = new File(fullPath); // 경로랑 파일 상태가 포함되어 잇음
		if (delFile.isFile()) {
			result = delFile.delete();
		}
		return result;
	}// end deleteFile

}// end class
