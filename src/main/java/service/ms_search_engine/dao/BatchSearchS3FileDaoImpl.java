package service.ms_search_engine.dao;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import service.ms_search_engine.dto.BatchSpectrumSearchDto;
import service.ms_search_engine.exception.S3DataUploadException;
import service.ms_search_engine.model.BatchSpectrumSearchModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.UUID;


@Component
public class BatchSearchS3FileDaoImpl implements BatchSearchS3FileDao{

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
        File peakListFile  = new File(batchSpectrumSearchDto.getPeakListFile().getOriginalFilename());
        File ms2SpectrumFile  = new File(batchSpectrumSearchDto.getMs2File().getOriginalFilename());
        try (FileOutputStream fos1 = new FileOutputStream(peakListFile)){
            fos1.write(batchSpectrumSearchDto.getPeakListFile().getBytes());
            }
        catch (Exception e) {
            throw new S3DataUploadException("PeakList file upload failed");
        }
        try (FileOutputStream fos2 = new FileOutputStream(ms2SpectrumFile)){
            fos2.write(batchSpectrumSearchDto.getMs2File().getBytes());
        }
        catch (Exception e) {
            throw new S3DataUploadException("MS2 spectrum file upload failed");
        }


        String baseFileName = generateFileNameByUUID();
        String peakListFileName = "peak_list_" + baseFileName + "." +  FilenameUtils.getExtension(batchSpectrumSearchDto.getPeakListFile().getOriginalFilename());
        String ms2SpectrumFileName = "ms2Spectrum_" + baseFileName + "." +  FilenameUtils.getExtension(batchSpectrumSearchDto.getMs2File().getOriginalFilename());

        //upload file to s3
        try{
            PutObjectRequest peakListRequest = new PutObjectRequest(bucketName, peakListFileName, peakListFile);
            PutObjectRequest ms2SpectrumRequest = new PutObjectRequest(bucketName, ms2SpectrumFileName, ms2SpectrumFile);
            //peakList
            ObjectMetadata peakListMetadata = new ObjectMetadata();
            peakListMetadata.setContentType("plain/" + FilenameUtils.getExtension(batchSpectrumSearchDto.getPeakListFile().getOriginalFilename()));
            peakListMetadata.addUserMetadata("Title", "File Upload - " + peakListFileName);
            peakListMetadata.setContentLength(peakListFile.length());
            peakListRequest.setMetadata(peakListMetadata);


            //ms2Spectrum
            ObjectMetadata ms2Metadata = new ObjectMetadata();
            ms2Metadata.setContentType("plain/" + FilenameUtils.getExtension(batchSpectrumSearchDto.getMs2File().getOriginalFilename()));
            ms2Metadata.addUserMetadata("Title", "File Upload - " + ms2SpectrumFileName);
            ms2Metadata.setContentLength(ms2SpectrumFile.length());
            peakListRequest.setMetadata(ms2Metadata);




            s3Client.putObject(peakListRequest);
            s3Client.putObject(ms2SpectrumRequest);


            batchSpectrumSearchDto.setMs2S3FileSrc(ms2SpectrumFileName);
            batchSpectrumSearchDto.setPeakListS3FileSrc(peakListFileName);

        } catch (Exception e) {
            throw new S3DataUploadException("PeakList file upload failed");
        } finally {
            peakListFile.delete();
            ms2SpectrumFile.delete();
        }



        return batchSpectrumSearchDto;
    }

    @Override
    public Boolean deleteFileByKey(String key) throws S3DataUploadException {
        try {
            s3Client.deleteObject(bucketName, key);
        }catch (Exception e){
            throw new S3DataUploadException("file delete failed");
        }
        return true;
    }

    public String generateFileNameByUUID() {
        UUID uuid = UUID.randomUUID();

        return  uuid.toString();
    }


}
