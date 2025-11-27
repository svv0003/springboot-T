package edu.thejoeun.common.util;

// 파일 이미지를 업로드 할 때, 변수이름을 상세히 작성하는 것이 좋다.
// 프로필 이미지, 게시물 이미지, 상품 이미지


//import lombok.Value;  // DB 관련 Value DB 컬럼값
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value; // 스프링부트 properties 에서 사용한 데이터
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@Slf4j
public class FileUploadService {
    // import org.springframework.beans.factory.annotation.Value;
    @Value("${file.upload.path}")
    private String uploadPath;

    public String uploadProfileImage(MultipartFile file) throws IOException {
        return "";
    }

}