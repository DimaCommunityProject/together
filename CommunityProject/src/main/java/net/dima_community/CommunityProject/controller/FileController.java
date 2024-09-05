package net.dima_community.CommunityProject.controller;

import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dima_community.CommunityProject.dto.board.BoardDTO;
import net.dima_community.CommunityProject.service.BoardService;

@Slf4j
@Controller
@RequiredArgsConstructor
public class FileController {
    private final BoardService boardService;

    // 첨부 파일 경로 요청
    @Value("${spring.servlet.multipart.location}")
    String uploadPath;

    // ================= 첨부파일 다운로드 ===================

    /**
     * 첨부파일 다운로드 함수
     * @param boardId
     * @param response
     * @return
     */
    @GetMapping("/download")
    public String download(@RequestParam(name = "boardId") Long boardId, HttpServletResponse response) {

        BoardDTO boardDTO = boardService.selectOne(boardId); // Board 엔티티

        String originalFileName = boardDTO.getOriginalFileName();
        String savedFileName = boardDTO.getSavedFileName();

        try {
            String tempName = URLEncoder.encode(originalFileName,StandardCharsets.UTF_8.toString());
            response.setHeader("Content-Disposition", "attachment;filname="+tempName);
            // 위 코드가 없을 경우 브라우저가 실행 가능한 파일(이미지 파일이 대표적인 예임)인 경우 브라우저 자체에서 오픈함
            // 즉, 위 코드는 브라우저 자체에서 실행되도록 하지 않고 다운받게 하기 위한 코드임 
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String fullPath = uploadPath+"/"+savedFileName;

        // 스트림 설정 (실제 다운로드)
        FileInputStream filein = null;
        ServletOutputStream fileout = null; // 원격지의 장소에 데이터를 쏘기 위함

        // Local에 있는 파일을 메모리로 끌어와야 함
        try {
            filein = new FileInputStream(fullPath); // 하드디스크->메모리에 올림(서버 입장에서의 로컬이기 때문에 input 작업임)
            fileout = response.getOutputStream();   // 웹에서 원격지의 데이터를 쏴주는 것. 로컬에서 벗어난 다른 쪽으로 데이터를 쏘는 역할

            FileCopyUtils.copy(filein, fileout);    // copy(원본, 내보낼 객체) : 원본을 읽어서 내보냄
            
            // 연 순서의 반대로 닫아야 함
            fileout.close();
            filein.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    

}
