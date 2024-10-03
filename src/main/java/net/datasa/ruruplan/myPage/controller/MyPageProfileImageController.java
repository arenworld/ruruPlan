package net.datasa.ruruplan.myPage.controller;

import lombok.extern.slf4j.Slf4j;
import net.datasa.ruruplan.myPage.service.MyPageService;
import net.datasa.ruruplan.security.AuthenticatedUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
@Slf4j
@RestController
@RequestMapping("/profile")
public class MyPageProfileImageController {

    @Value("${upload.path}") // 업로드 경로 설정
    private String uploadPath;

    private final MyPageService myPageService;

    public MyPageProfileImageController(MyPageService myPageService) {
        this.myPageService = myPageService;
    }

    // 프로필 이미지 업로드 처리
    @PostMapping("/uploadImage")
    public Map<String, Object> uploadProfileImage(
            @RequestParam("profileImage") MultipartFile file,
            @AuthenticationPrincipal AuthenticatedUser user) {
        log.debug("로그인된 사용자: {}", user.getUsername());
        Map<String, Object> response = new HashMap<>();
        if (!file.isEmpty()) {
            try {
                // 파일 저장
                String originalFilename = file.getOriginalFilename();
                String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                String savedFileName = UUID.randomUUID().toString() + extension;
                File saveFile = new File(uploadPath + "/" + savedFileName);
                file.transferTo(saveFile);

                // 프로필 이미지 URL을 DB에 저장
                String profileImageUrl = "/uploads/" + savedFileName;
                log.debug("저장할 이미지 경로: {}", profileImageUrl);
                myPageService.updateProfileImage(user.getUsername(), profileImageUrl);

                // 성공 응답 반환
                response.put("success", true);
                response.put("profileImageUrl", profileImageUrl);  // 업로드된 이미지 경로 반환
            } catch (IOException e) {
                e.printStackTrace();
                response.put("success", false);
            }
        } else {
            response.put("success", false);
        }

        return response;
    }

}
