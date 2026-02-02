package com.example.madcamp.peogayo.Back.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class FileService {

    // 1. 업로드 디렉토리 설정 (프로젝트 폴더 내 uploads 폴더)
    private final String uploadDir = System.getProperty("user.dir") + File.separator + "uploads" + File.separator;

    public String uploadFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        // 2. 폴더가 없으면 생성
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            if (dir.mkdirs()) {
                System.out.println("Upload directory created: " + uploadDir);
            }
        }

        // 3. 파일명 중복 방지
        String originalFilename = file.getOriginalFilename();
        // 확장자 추출
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String savedFilename = UUID.randomUUID().toString() + "_" + originalFilename;

        // 4. 파일 저장
        File dest = new File(uploadDir + savedFilename);

        file.transferTo(dest);

        // 5. DB에 저장할 접근용 URL 반환
        return "/uploads/" + savedFilename;
    }
}