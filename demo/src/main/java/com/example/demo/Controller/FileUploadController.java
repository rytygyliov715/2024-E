package com.example.demo.Controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class FileUploadController {

    // 将上传的文件保存到此文件夹

    // 路径设置为配置文件，方便重新打包
    @Value("${file.upload.path}")
    private String UPLOADED_FOLDER;

    // 测试文件上传
    @GetMapping("/test/fileupload")
    public String uploadForm() {
        return "upload";
    }

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