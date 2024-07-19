package com.example.demo.Controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class indexController {

    // 路径设置为配置文件，方便重新打包
    @Value("${file.upload.path}")
    private String UPLOADED_FOLDER;

    // 项目主页
    @GetMapping("/index")
    public String index() {
        return "index";
    }

    // helloworld
    @GetMapping("/test/helloworld")
    public ResponseEntity<String> helloWorld() {
        return ResponseEntity.ok("Hello World");
    }

    // 文件上传
    @GetMapping("/test/fileupload")
    public String uploadForm() {
        return "upload";
    }

    // 文件上传状态
    @GetMapping("/uploadStatus")
    public String uploadStatus() {
        // 在这里添加你的逻辑
        return "uploadStatus";
    }

    @PostMapping("/test/fileupload")
    // @RequestParam("file") MultipartFile file: 用于接收上传的文件
    public String fileUpload(@RequestParam("name") String name,
            @RequestParam("file") MultipartFile file,
            RedirectAttributes redirectAttributes) {
        // 判断文件是否为空
        if (file.isEmpty()) {
            // 重定向到/uploadStatus
            redirectAttributes.addFlashAttribute("message",
                    "请选择要上传的文件");
            return "redirect:/uploadStatus";
        }

        // 保存文件
        try {
            byte[] bytes = file.getBytes();
            Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
            Files.write(path, bytes);

            redirectAttributes.addFlashAttribute("message",
                    "您已成功上传 '" + file.getOriginalFilename() + "'");

        } catch (IOException e) {
            e.printStackTrace();
        }

        // 重定向到/uploadStatus
        return "redirect:/uploadStatus";
    }
}
