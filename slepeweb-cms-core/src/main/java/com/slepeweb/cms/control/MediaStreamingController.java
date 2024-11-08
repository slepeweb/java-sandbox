package com.slepeweb.cms.control;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.Media;
import com.slepeweb.cms.bean.User;
import com.slepeweb.cms.service.ItemService;
import com.slepeweb.cms.service.MediaFileService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/stream")
public class MediaStreamingController {
	
	public static final String VIDEO_TYPE = "video/mp4";
    public static final String IMAGE_TYPE = "image/jpg";
    
	@Autowired private ItemService itemService;
	@Autowired private MediaFileService mediaFileService;
    

    @GetMapping(value="/video/media", produces=VIDEO_TYPE)
    public ResponseEntity<byte[]> streamVideoGivenMediaAttr(
            @RequestHeader HttpHeaders headers, 
            HttpServletRequest req) {

    	Media m = (Media) req.getAttribute("_media");
   		return handle(m, VIDEO_TYPE, headers, req);
    }
    
    @GetMapping(value="/video/itemid/{itemId}", produces=VIDEO_TYPE)
    public ResponseEntity<byte[]> streamVideoGivenItemId(
            @RequestHeader HttpHeaders headers, 
            @PathVariable long itemId,
            HttpServletRequest req) {

   		return handle(this.itemService.getItem(itemId), VIDEO_TYPE, headers, req);
    }
    
    @GetMapping(value="/video/item", produces=VIDEO_TYPE)
    public ResponseEntity<byte[]> streamVideoGivenItemAttr(
            @RequestHeader HttpHeaders headers, 
            HttpServletRequest req) {

    	Item i = (Item) req.getAttribute("_item");
   		return handle(i, VIDEO_TYPE, headers, req);
    }
    
    @GetMapping(value="/image/media", produces=IMAGE_TYPE)
    public ResponseEntity<byte[]> streamImageGivenMediaAttr(
            @RequestHeader HttpHeaders headers, 
            HttpServletRequest req) {

    	Media m = (Media) req.getAttribute("_media");
   		return handle(m, IMAGE_TYPE, headers, req);
    }
    
    @GetMapping(value="/image/itemid/{itemId}", produces=IMAGE_TYPE)
    public ResponseEntity<byte[]> streamImageGivenItemId(
            @RequestHeader HttpHeaders headers, 
            @PathVariable long itemId,
            HttpServletRequest req) {

   		return handle(this.itemService.getItem(itemId), IMAGE_TYPE, headers, req);
    }
    
    @GetMapping(value="/image/item", produces=IMAGE_TYPE)
    public ResponseEntity<byte[]> streamImageGivenItemAttr(
            @RequestHeader HttpHeaders headers, 
            HttpServletRequest req) {

    	Item i = (Item) req.getAttribute("_item");
   		return handle(i, IMAGE_TYPE, headers, req);
    }
    
	private ResponseEntity<byte[]> handle(Item item, String mimeType, HttpHeaders headers, HttpServletRequest req) {

		if (item == null || !item.getType().isMedia()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
		
		item.setUser((User) req.getSession().getAttribute("_user"));
		
		if (! item.isAccessible()) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
		}
		else {
			String viewParam = req.getParameter("view");
			boolean thumbnailRequired = false;
			if (StringUtils.isNotBlank(viewParam)) {
				thumbnailRequired = viewParam.equals("thumbnail");
			}

			Media m = thumbnailRequired ? item.getThumbnail() : item.getMedia();
			if (m == null || ! m.isBinaryContentLoaded()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
			}
			
			return handle(m, mimeType, headers, req);
		}
	}
	
	private ResponseEntity<byte[]> handle(Media m, String mimeType, HttpHeaders headers, HttpServletRequest req) {
    	
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
