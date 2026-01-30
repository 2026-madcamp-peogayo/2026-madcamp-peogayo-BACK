package com.example.madcamp.peogayo.Back.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class FileService {

    // 프로젝트 루트 경로 밑에 uploads 폴더 생성
    private final String uploadDir = System.getProperty("user.dir") + "/uploads/";

    public String uploadFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        // 폴더가 없으면 생성
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 파일명 중복 방지
        String originalFilename = file.getOriginalFilename();
        String savedFilename = UUID.randomUUID() + "_" + originalFilename;

        // 파일 저장
        File dest = new File(uploadDir + savedFilename);
        file.transferTo(dest);

        // DB에 저장할 접근 URL 반환
        return "/uploads/" + savedFilename;
    }
}