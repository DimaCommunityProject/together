package net.dima_community.CommunityProject.util;

import java.io.File;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

public class FileService {
    
    // 1) 서버에 디렉토리가 없으면 디렉토리 생성
    // 2) 원본 파일명을 꺼내서 저장 파일명(랜덤값 or 밀리세컨)을 새롭게 작성
    // 3) 디렉토리에 파일을 저장하는 작업 수행
    // 4) 저장 파일명을 반환하여 DB에 저장할 수 있게 함

    /**
     * 사용자가 올린 첨부파일을 로컬 서버에 저장하는 함수
     * @param uploadFile (저장할 파일)
     * @param uploadPath (저장할 경로)
     * @return savedFileName (저장 파일명)
     */
    public static String saveFile(MultipartFile uploadFile, String uploadPath){
        // 파일이 첨부되면 디렉토리가 있는지 확인
        // 없으면 생성, 있으면 그대로 사용
        if(!uploadFile.isEmpty()){
            File path = new File(uploadPath);
            if (!path.isDirectory()) {
                path.mkdirs();
            }
        }

        // 원본 파일명 추출
        String originalFileName = uploadFile.getOriginalFilename();

        // 랜덤값 발생
        String uuid = UUID.randomUUID().toString();

        // 저장 파일명 생성
        String filename;
        String ext;
        String savedFileName;

        // '.' 위치 반환. 만약 확장자가 없는 파일이면 -1 반환
        int position = originalFileName.lastIndexOf("."); // 점 위치 반환
        // 확장자 없는 경우
        if (position==-1) ext="";
        else ext = "."+originalFileName.substring(position+1);

        filename = originalFileName.substring(0, position);
        savedFileName = filename+"_"+uuid+ext;

        // 로컬 서버 저장공간에 파일을 저장하는 작업
        File serverFile = null;
        serverFile = new File(uploadPath+"/"+savedFileName);

        try {
            uploadFile.transferTo(serverFile);
        } catch (Exception e) {
            savedFileName=null; // error 발생 시 DB, hdd에 저장되면 안됨 -> null로 변환
            e.printStackTrace();
        }

        return savedFileName; // 저장 파일명 반환
    }

    /**
     * 저장장치에 저장된 파일 삭제 
     * @param fullPath (경로 + 파일명)
     * @return 삭제 여부
     */
    public static boolean deleteFile(String fullPath){
        boolean result = false; // 삭제 여부 반환

        File delFile = new File(fullPath);
        if (delFile.isFile()) {
            result = delFile.delete();
        }

        return result;
    }

}
