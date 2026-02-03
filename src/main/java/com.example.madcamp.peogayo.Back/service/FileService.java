package com.example.madcamp.peogayo.Back.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class FileService {

    // 서버와 로컬 환경에 따른 절대 경로 설정
    private final String getUploadPath() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return "C:/peogayo_uploads/";
        } else {
            // ec2-user 계정 폴더 내부에 저장
            return "/home/ec2-user/peogayo_uploads/";
        }
    }

    public String uploadFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String uploadDir = getUploadPath();

        // 1. 폴더가 없으면 생성
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 2. 파일명 중복 방지 (UUID 사용)
        String originalFilename = file.getOriginalFilename();
        String savedFilename = UUID.randomUUID().toString() + "_" + originalFilename;

        // 3. 파일 물리적 저장
        File dest = new File(uploadDir + savedFilename);
        file.transferTo(dest);

        // 4. DB에 저장할 URL 반환 (WebMvcConfig의 핸들러와 매핑됨)
        return "/uploads/" + savedFilename;
    }
}