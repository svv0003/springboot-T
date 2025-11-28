package edu.thejoeun.common.util;

// 파일 이미지를 업로드 할 때, 변수이름을 상세히 작성하는 것이 좋다.
// 프로필 이미지, 게시물 이미지, 상품 이미지


//import lombok.Value;  // DB 관련 Value DB 컬럼값
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value; // 스프링부트 properties 에서 사용한 데이터
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Slf4j
public class FileUploadService {
    // import org.springframework.beans.factory.annotation.Value;
    @Value("${file.upload.path}")
    private String uploadPath;

    /**
     * 프로필 이미지 업로드
     * @param file          업로드할 이미지 파일
     * @return              저장된 파일의 경로 (DB에 저장할 상대 경로)
     * @throws IOException  파일 처리 중 오류 발생 시 예외 처리
     */
    public String uploadProfileImage(MultipartFile file) throws IOException {
        // 파일이 비어있는지 확인한다.
        if(file.isEmpty()) {
            throw new IOException("업로드할 파일이 없습니다.");
        }
        /*
        업로드 디렉토리 생성
        (폴더가 존재하지 않는 경우 디렉토리 = 폴더 컴퓨터 만든 회사에서 지칭하는 명칭이 다르다.)
         */
        File uploadDir = new File(uploadPath);
        if(!uploadDir.exists()) {
            boolean created = uploadDir.mkdirs();
            if(!created) {
                throw new IOException("업로드 디렉토리 생성에 실패했습니다.");
            }
            log.info("업로드 디렉토리 생성 : {}", uploadPath);
        }
        /*
        프로필 폴더 / 유저 폴더 / 프로필 이미지 구조
        -> 같은 파일명의 이미지로 교체하면 이름 변경하거나 (1) 숫자를 추가해야 해서
        -> 개발자가 원본 파일명, 확장자 추출하여 임의로 수정한다.
        -> originalFileName 변수명 사용한다.
         */
        String originalFilename = file.getOriginalFilename();
        if(originalFilename == null || originalFilename.isEmpty()) {
            throw new IOException("파일명이 유효하지 않습니다.");
        }
        String extension = "";
        int lastDotIndex = originalFilename.lastIndexOf('.');
        if(lastDotIndex > 0) {
            extension = originalFilename.substring(lastDotIndex + 1);
        }
        // UUID 사용하여 고유 파일명 생성한다.
        String uniqueFileName = UUID.randomUUID().toString() + "." + extension;
        // 파일 저장 경로 설정한다.
        Path filePath = Paths.get(uploadPath, uniqueFileName);
        /*
        파일 저장한다.
        이미지 파일처럼 긴 문자열로 이루어진 파일은 반드시 업로드, 다운로드 중에 오류가 발생할 수 있어서 try-catch문을 사용해야 한다.
         */
        try {
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            log.info("프로필 이미지 업로드 성공 : {} -> {}", originalFilename, uniqueFileName);
        } catch (IOException e) {
            log.error("파일 저장 중 오류 발생 : {}", e.getMessage());
            throw new  IOException("파일 저장을 실패했습니다. " + e.getMessage());
        }
        // DB에서 저장할 상대 경로를 반환한다. (웹에서 접근 가능한 경로)
        return "/profile_images/"+uniqueFileName;
    }

}
