package io.travel.map.controller;


import io.travel.map.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Controller
@RequiredArgsConstructor
public class FileUploadController {

    private final S3Service s3Service;

    // 템플릿 렌더링용 GET
    @GetMapping("/upload")
    public String uploadForm(){
        return "upload";
    }


    // 백엔드 테스트용
    @PostMapping("/upload")
    // public ResponseEntity<String> upload (@RequestParam("file") MultipartFile file) 이게 배포용
    public String handleFileUpload(@RequestParam("file") MultipartFile file, Model model) throws IOException {
        try {
            String fileName = s3Service.uploadFile(file);
            String url = s3Service.geFileUrl(fileName);
            log.info("file url = {}", url);
            model.addAttribute("url", url);
            //  return ResponseEntity.ok(url);
        } catch (IOException e){
            model.addAttribute("url", "Upload failed:" + e.getMessage());
        }
        return "upload";
    }
}
