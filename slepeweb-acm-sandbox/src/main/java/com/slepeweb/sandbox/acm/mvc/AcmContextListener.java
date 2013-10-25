package com.slepeweb.sandbox.acm.mvc;

import java.util.Locale;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.web.context.ServletContextAware;

import com.slepeweb.sandbox.acm.constants.RequestAttribute;

/**
 * Application Startup code should be placed here.
 * 
 * @author adam
 * 
 */
public class AcmContextListener implements ApplicationListener<ContextRefreshedEvent>, ServletContextAware {

	private ServletContext servletContext;
	private static Logger LOG = Logger.getLogger(AcmContextListener.class);

	@Value("${site-group}")
	private String siteGroupName;

	@Value("${ACMConnectionBean.serverName:Unknown}")
	private String serverName;

	@Value("${ACMConnectionBean.isLiveServer:false}")
	private boolean isLiveServer;

	@Value("${ACMConnectionBean.serverId:unknown}")
	private String serverId;

	private boolean initialised = false;

	public void onApplicationEvent(ContextRefreshedEvent event) {

		// TODO this should not use isRootContext instead should be isSpringContext
		if (!initialised && !isRootContext(event)) {
			initialised = true;

			try {

				LOG.info("\n==========================================================================\n"
						+ "Initialising the " + siteGroupName + " web application ..."
						+ "\n==========================================================================\n");

				setDefaultLocale();
				configServerName();
				servletContext.setAttribute(RequestAttribute.IS_LIVE_SERVER_ATT_NAME, isLiveServer);
				LOG.info("isLiveServer :" + isLiveServer);
				LOG.info("serverId :" + serverId);

				LOG.info("\n==========================================================================\n"
						+ "The ACM webapp has been successfully initialised"
						+ "\n==========================================================================\n");
			} catch (Exception e) {
				LOG.fatal("Failed to correctly initialise the " + this.siteGroupName + " web application", e);
				servletContext.setAttribute(RequestAttribute.WEBAPP_STATUS_ATT_NAME, new Boolean(false));
			}
		}
	}

	private boolean isRootContext(ContextRefreshedEvent event) {
		ApplicationContext context = event.getApplicationContext();
		return context.getParent() == null;
	}

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	private void setDefaultLocale() {
		// Set default locale - VERY IMPORTANT to ensure that ResourceBundles work correctly
		Locale.setDefault(new Locale("en", "GB"));
	}

	private void configServerName() {
		// Server name
		servletContext.setAttribute(RequestAttribute.SERVER_ID_ATT_NAME, serverId);
	}

}
