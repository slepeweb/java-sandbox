package com.slepeweb.cms.control;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import com.slepeweb.cms.bean.Media;
import com.slepeweb.cms.service.MediaFileService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/stream")
public class MediaStreamingController {
	
    public static final String VIDEO_TYPE = "video/mp4";
    public static final String IMAGE_TYPE = "image/jpg";
    
	@Autowired private MediaFileService mediaFileService;
    

    @GetMapping(value="/video", produces=VIDEO_TYPE)
    public ResponseEntity<byte[]> streamVideo(
            @RequestHeader HttpHeaders headers, 
            HttpServletRequest req) {

    		return handle(VIDEO_TYPE, headers, req);
    }
    
    @GetMapping(value="/image", produces=IMAGE_TYPE)
    public ResponseEntity<byte[]> streamImage(
            @RequestHeader HttpHeaders headers, 
            HttpServletRequest req) {

    		return handle(IMAGE_TYPE, headers, req);
    }
    
    private ResponseEntity<byte[]> handle(String mimeType, HttpHeaders headers, HttpServletRequest req) {
    	
    	Media m = (Media) req.getAttribute("_media");
		String filePath = this.mediaFileService.getRepositoryFilePath(m.getFolder(), m.getRepositoryFileName());
		File f = new File(filePath);
        long fileSize = f.length();
        
        List<HttpRange> httpRanges = headers.getRange();

        try (RandomAccessFile raf = new RandomAccessFile(f, "r")) {
            if (httpRanges.isEmpty()) {
            	
                // Full video request
                byte[] fullData = new byte[(int) fileSize];
                raf.readFully(fullData);
                
                return ResponseEntity.ok()
                        .contentType(MediaType.valueOf(mimeType))
                        .contentLength(fileSize)
                        .body(fullData);

            } 
            else {
                // Partial content
                HttpRange range = httpRanges.get(0);
                long start = range.getRangeStart(fileSize);
                long end = range.getRangeEnd(fileSize);

                long chunkSize = end - start + 1;
                byte[] data = new byte[(int) chunkSize];

                raf.seek(start);
                raf.readFully(data);
                
                String contentRange = String.format("bytes %d-%d/%d", start, end, fileSize);

                return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                        .header(HttpHeaders.CONTENT_RANGE, contentRange)
                        .contentType(MediaType.valueOf(mimeType))
                        .contentLength(chunkSize)
                        .body(data);
            }

        } 
        catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
