package org.udg.pds.springtodo.controller;

import io.minio.MinioClient;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.udg.pds.springtodo.Global;
import org.udg.pds.springtodo.controller.exceptions.ControllerException;
import org.udg.pds.springtodo.service.UserService;

import javax.servlet.http.HttpSession;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.UUID;

@RequestMapping(path = "/images")
@RestController
public class ImageController extends BaseController {

    @Autowired
    Global global;

    @Autowired
    UserService userService;

    @PostMapping
    public String upload(HttpSession session, @RequestParam("file") MultipartFile file) {
        MinioClient minioClient = global.getMinioClient();
        if (minioClient == null)
            throw new ControllerException("Minio client not configured");

        try {
            // Handle the body of that part with an InputStream
            InputStream istream = file.getInputStream();
            String contentType = file.getContentType();
            UUID imgName = UUID.randomUUID();

            String objectName = imgName + "." + FilenameUtils.getExtension(file.getOriginalFilename());
            // Upload the file to the bucket with putObject
            minioClient.putObject(global.getMinioBucket(),
                    objectName,
                    istream,
                    contentType);

            // Update the user image in database
            Long userId = getLoggedUser(session);
            userService.updateProfileImage(userId, objectName);

        } catch (Exception e) {
            throw new ControllerException("Error saving file: " + e.getMessage());
        }

        return BaseController.OK_MESSAGE;
    }

    @GetMapping("/{filename:.+}")
    public ResponseEntity<InputStreamResource> download(@PathVariable("filename") String filename) {

        MinioClient minioClient = global.getMinioClient();
        if (minioClient == null)
            throw new ControllerException("Minio client not configured");

        try {
            InputStream file = minioClient.getObject(global.getMinioBucket(), filename);
            InputStreamResource body = new InputStreamResource(file);
            HttpHeaders headers = new HttpHeaders();
            // headers.setContentLength(body.contentLength());
            // headers.setContentDispositionFormData("attachment", "test.csv");
            // Ok for most cases
            String contentType = URLConnection.guessContentTypeFromName(filename);
            // For some rebels like webp
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            headers.setContentType(MediaType.parseMediaType(contentType));

            return ResponseEntity.ok().headers(headers).body(body);

        } catch (Exception e) {
            throw new ControllerException("Error downloading file: " + e.getMessage());
        }
    }

}
