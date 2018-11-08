package com.pinyougou.shop.controller;

import org.apache.commons.io.FilenameUtils;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
public class UploadController {

    @Value("${fileServerUrl}")
    private String fileServerUrl;

    @PostMapping("/upload")
    public Map<String, Object> upload(@RequestParam("file") MultipartFile multipartFile) {
        Map<String, Object> data = new HashMap<>();
        data.put("status", 500);
        try {
            String conf_fileName = this.getClass().getResource("/fastdfs_client.conf").getPath();
            ClientGlobal.init(conf_fileName);
            String filename = multipartFile.getOriginalFilename();
            StorageClient storageClient = new StorageClient();
            String[] arr = storageClient.upload_file(multipartFile.getBytes(), FilenameUtils.getExtension(filename), null);
            StringBuilder stringBuilder = new StringBuilder(fileServerUrl);
            for (String s : arr) {
                stringBuilder.append("/" + s);
            }
            data.put("status",200);
            data.put("url",stringBuilder.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return data;
    }
}
