package com.sparta.fishingload_backend.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.sparta.fishingload_backend.dto.UserImageResponseDto;
import com.sparta.fishingload_backend.entity.User;
import com.sparta.fishingload_backend.entity.UserImage;
import com.sparta.fishingload_backend.repository.UserImageRepository;
import com.sparta.fishingload_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Log4j2
public class ImageService {

    private final UserImageRepository userImageRepository;
    private final UserRepository userRepository;
    private final AmazonS3Client amazonS3Client;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Transactional
    public List<UserImageResponseDto> userImageUpload(List<MultipartFile> multipartFiles, User user) {
        User selectUser = findUser(user.getUserId());
        List<UserImageResponseDto> s3files = new ArrayList<>();

        String uploadFilePath = "userProfil";

        for (MultipartFile multipartFile : multipartFiles) {
            String originalFileName = multipartFile.getOriginalFilename();
            String uploadFileName = getUuidFileName(originalFileName);
            String uploadFileUrl = "";

            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(multipartFile.getSize());
            objectMetadata.setContentType(multipartFile.getContentType());

            try (InputStream inputStream = multipartFile.getInputStream()) {
                String keyName = uploadFilePath + "/" + uploadFileName; // userProfil/파일.확장자

                // S3에 폴더 및 파일 업로드
                amazonS3Client.putObject(
                        new PutObjectRequest(bucketName, keyName, inputStream, objectMetadata)
                );

                // S3에 업로드한 폴더 및 파일 URL
                uploadFileUrl = amazonS3Client.getUrl(bucketName, keyName).toString();

                UserImage userImage = userImageRepository.findByUser(selectUser);
                if (userImage == null) {
                    userImageRepository.save(new UserImage(keyName, uploadFileUrl, selectUser));
                } else {
                    amazonS3Client.deleteObject(bucketName, userImage.getImagePath());
                    userImage.update(keyName, uploadFileUrl);
                }
            } catch (IOException e) {
                e.printStackTrace();
                log.error("Filed upload failed", e);
            }

            s3files.add(new UserImageResponseDto(originalFileName,
                    uploadFileName, uploadFilePath, uploadFileUrl));
        }

        return s3files;
    }

    // UUID 파일명 반환
    private String getUuidFileName(String fileName) {
        String ext = fileName.substring(fileName.indexOf(".") + 1);
        return UUID.randomUUID().toString() + "." + ext;
    }

    private User findUser(String userId) {
        return userRepository.findByUserIdAndAccountUseTrue(userId).orElseThrow(() ->
                new NullPointerException("해당 유저는 존재하지 않습니다.")
        );
    }
}
