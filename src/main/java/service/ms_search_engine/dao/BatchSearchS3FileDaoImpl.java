package service.ms_search_engine.dao;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.policy.Resource;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import service.ms_search_engine.dto.BatchSpectrumSearchDto;
import service.ms_search_engine.exception.S3DataDownloadException;
import service.ms_search_engine.exception.S3DataUploadException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;


@Component
public class BatchSearchS3FileDaoImpl implements BatchSearchS3FileDao {

    @Value("${aws.s3.bucket.name}")
    private String bucketName;

    private final AmazonS3 s3Client;


    @Autowired
    public BatchSearchS3FileDaoImpl(AmazonS3 s3Client) {
        this.s3Client = s3Client;
    }


    @Override
    public BatchSpectrumSearchDto postFileUpload(BatchSpectrumSearchDto batchSpectrumSearchDto) throws S3DataUploadException {
        //convert multipartFile to File
        String baseFileName = generateFileNameByUUID();

        //peakList
        if (batchSpectrumSearchDto.getPeakListFile() != null) {
            File peakListFile = new File(batchSpectrumSearchDto.getPeakListFile().getOriginalFilename());
            try (FileOutputStream fos1 = new FileOutputStream(peakListFile)) {
                fos1.write(batchSpectrumSearchDto.getPeakListFile().getBytes());
            } catch (Exception e) {
                boolean isDeletePeakListFile = peakListFile.delete();
                throw new S3DataUploadException("PeakList file upload failed: " + e);
            }
            String peakListFileName = "peak_list_" + baseFileName + "." + FilenameUtils.getExtension(batchSpectrumSearchDto.getPeakListFile().getOriginalFilename());
            //upload file to s3
            try {
                PutObjectRequest peakListRequest = new PutObjectRequest(bucketName, peakListFileName, peakListFile);
                //peakList
                ObjectMetadata peakListMetadata = new ObjectMetadata();
                peakListMetadata.setContentType("plain/" + FilenameUtils.getExtension(batchSpectrumSearchDto.getPeakListFile().getOriginalFilename()));
                peakListMetadata.addUserMetadata("Title", "File Upload - " + peakListFileName);
                peakListMetadata.setContentLength(peakListFile.length());
                peakListRequest.setMetadata(peakListMetadata);
                s3Client.putObject(peakListRequest);
                batchSpectrumSearchDto.setPeakListS3FileSrc(peakListFileName);

            } catch (Exception e) {
                throw new S3DataUploadException("PeakList file upload failed: " + e);
            } finally {
                boolean isDeletePeakListFile = peakListFile.delete();
            }

        }

        //ms2Spectrum
        if (batchSpectrumSearchDto.getMs2File() != null) {
            File ms2SpectrumFile = new File(batchSpectrumSearchDto.getMs2File().getOriginalFilename());
            try (FileOutputStream fos2 = new FileOutputStream(ms2SpectrumFile)) {
                fos2.write(batchSpectrumSearchDto.getMs2File().getBytes());
            } catch (Exception e) {
                boolean isDeleteMs2File = ms2SpectrumFile.delete();
                throw new S3DataUploadException("MS2 spectrum file upload failed");
            }
            String ms2SpectrumFileName = "ms2Spectrum_" + baseFileName + "." + FilenameUtils.getExtension(batchSpectrumSearchDto.getMs2File().getOriginalFilename());
            try {
                //upload file to s3
                PutObjectRequest ms2SpectrumRequest = new PutObjectRequest(bucketName, ms2SpectrumFileName, ms2SpectrumFile);
                ObjectMetadata ms2Metadata = new ObjectMetadata();
                ms2Metadata.setContentType("plain/" + FilenameUtils.getExtension(batchSpectrumSearchDto.getMs2File().getOriginalFilename()));
                ms2Metadata.addUserMetadata("Title", "File Upload - " + ms2SpectrumFileName);
                ms2Metadata.setContentLength(ms2SpectrumFile.length());
                s3Client.putObject(ms2SpectrumRequest);
                batchSpectrumSearchDto.setMs2S3FileSrc(ms2SpectrumFileName);
            } catch (Exception e) {
                throw new S3DataUploadException("Ms2 file upload failed: " + e);
            } finally {
                boolean isDeleteMs2File = ms2SpectrumFile.delete();
            }
        }

        //resultPeakList
        if (batchSpectrumSearchDto.getResultPeakListFile()!= null) {
            File resultPeakListFile = new File(batchSpectrumSearchDto.getResultPeakListFile().getName());
            try (FileOutputStream fos3 = new FileOutputStream(resultPeakListFile)) {
                fos3.write(batchSpectrumSearchDto.getResultPeakListFile().getBytes());
            } catch (Exception e) {
                boolean isDeleteResultFile = resultPeakListFile.delete();
                throw new S3DataUploadException("Results spectrum file upload failed: " + e);
            }
            String resultPeakListFileName = "ResultPeakList_" + baseFileName + "." + FilenameUtils.getExtension(batchSpectrumSearchDto.getResultPeakListFile().getName());
            try {
                //upload file to s3
                PutObjectRequest resultPeakListRequest = new PutObjectRequest(bucketName, resultPeakListFileName, resultPeakListFile);
                ObjectMetadata resultPeakListMetadata = new ObjectMetadata();
                resultPeakListMetadata.setContentType("plain/" + FilenameUtils.getExtension(batchSpectrumSearchDto.getResultPeakListFile().getName()));
                resultPeakListMetadata.addUserMetadata("Title", "File Upload - " + resultPeakListFileName);
                resultPeakListMetadata.setContentLength(resultPeakListFile.length());
                s3Client.putObject(resultPeakListRequest);
                batchSpectrumSearchDto.setResultPeakListS3FileSrc(resultPeakListFileName);
            } catch (Exception e) {
                throw new S3DataUploadException("Ms2 file upload failed: " + e);
            } finally {
                boolean isDeleteResultFile = resultPeakListFile.delete();
            }
        }



        return batchSpectrumSearchDto;
    }

    @Override
    public Boolean deleteFileByKey(String key) throws S3DataUploadException {
        try {
            s3Client.deleteObject(bucketName, key);
        } catch (Exception e) {
            throw new S3DataUploadException("file delete failed: " + e);
        }
        return true;
    }

    public String generateFileNameByUUID() {
        UUID uuid = UUID.randomUUID();

        return uuid.toString();
    }

    @Override
    public UrlResource downloadFileByFileName(String fileName) throws S3DataDownloadException, IOException {
        if (bucketIsEmpty()) {
            throw new S3DataDownloadException("Requested bucket does not exist or is empty");
        }
//        String storedFileNameDir = downloadFileDir + fileName;
        S3Object object = s3Client.getObject(bucketName, fileName);
        try (S3ObjectInputStream s3is = object.getObjectContent()) {
            try (FileOutputStream fileOutputStream = new FileOutputStream(fileName)) {
                byte[] read_buf = new byte[1024];
                int read_len = 0;
                while ((read_len = s3is.read(read_buf)) > 0) {
                    fileOutputStream.write(read_buf, 0, read_len);
                }
            }
            Path pathObject = Paths.get(fileName);
            UrlResource urlResource = new UrlResource(pathObject.toUri());

            if (urlResource.exists() || urlResource.isReadable()) {
                return urlResource;
            } else {
                throw new S3DataDownloadException("Could not find the file!");
            }
        }

    }

    private boolean bucketIsEmpty() {

        ListObjectsV2Result result = s3Client.listObjectsV2(this.bucketName);
        if (result == null) {
            return false;
        }
        List<S3ObjectSummary> objects = result.getObjectSummaries();
        return objects.isEmpty();
    }


}
