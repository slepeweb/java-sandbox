package com.slepeweb.cms.utils;

import java.nio.file.Files;
import java.nio.file.Path;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.slepeweb.cms.bean.SiteConfigCache;
import com.slepeweb.cms.bean.SiteConfigProperty;
import com.slepeweb.cms.service.CryptoService;
import com.slepeweb.cms.service.SiteConfigService;

/*
 * This is a bean creation factory, currently defining a bean named aesKey.
 */

@Configuration
public class SpringBeanFactory {
	
	private Logger LOG = Logger.getLogger(SpringBeanFactory.class);
	
	@Autowired private CryptoService cryptoService;
	@Autowired private SiteConfigService siteConfigService;
	
	@Bean
    public SecretKey aesKey() throws Exception {
        String keyFile = System.getProperty("cms.keyfile", System.getenv("CMS_KEYFILE"));
        String msg;
        
        if (keyFile != null) {
    		Path keyPath = Path.of(keyFile);
	        byte[] keyBytes = Files.readAllBytes(Path.of(keyFile));
	        
	        if (keyBytes.length > 0) {
		        LOG.info("Successfully read the key file");
		        return new SecretKeySpec(keyBytes, "AES");
	        }
	        
	        LOG.error("Key file (CMS_KEYFILE) is empty; generating new key");
	        SecretKey key = this.cryptoService.generateKey();
            Files.write(keyPath, key.getEncoded());

	        return key;
        }
        
        msg = "Environment variable CMS_KEYFILE not defined";
        LOG.error(msg);
        throw new IllegalStateException(msg);
    }
	
	@Bean
	public SiteConfigCache siteConfigCache() {
		SiteConfigCache cache = new SiteConfigCache();
		
		for (SiteConfigProperty scp : this.siteConfigService.getAll()) {
			LOG.info(scp);
			cache.put(scp.getSiteId(), scp);
		}
		
		return cache;
	}
}
