package com.infinity.fashionity.image.repository;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.infinity.fashionity.image.dto.ImageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.File;

@Repository
@RequiredArgsConstructor
public class ImageRepositoryWithS3 implements ImageRepository{
    private final AmazonS3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Override
    public ImageDTO imageSave(File file, String fileName) {
        s3Client.putObject(new PutObjectRequest(bucket, fileName, file)
                .withCannedAcl(CannedAccessControlList.PublicRead));
        return ImageDTO.builder()
                        .fileUrl(s3Client.getUrl(bucket,fileName).toString())
                .fileName(fileName)
                .build();
    }

    @Override
    public void imageRemove(String fileName) {
        if(s3Client.doesObjectExist(bucket,fileName)){
            s3Client.deleteObject(bucket,fileName);
        }
    }
}
