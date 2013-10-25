package com.slepeweb.sandbox.acm.mvc;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;

import com.mediasurface.client.ConnectionException;
import com.mediasurface.client.InitException;
import com.mediasurface.client.Mediasurface;
import com.mediasurface.client.servlets.IAttributeNames;
import com.mediasurface.general.AuthorizationException;
import com.slepeweb.sandbox.acm.constants.RequestAttribute;

@Controller
public class AcmControllerServlet extends MediasurfaceControllerServlet {
	private static final long serialVersionUID = 1L;
	private static Logger LOG = Logger.getLogger( AcmControllerServlet.class );
	private Map<String, String> properties = null;

	@Override
	public void init( ServletConfig config ) throws ServletException {
		super.init( config );
	}

	public void service( HttpServletRequest req, HttpServletResponse res ) throws javax.servlet.ServletException, java.io.IOException {

		LOG.info( "Requesting :" + req.getRequestURL() );

		String mscPara = req.getParameter( IAttributeNames.IN_SITU_EDITING );
		String mscAttrib = ( String ) req.getAttribute( IAttributeNames.IN_SITU_EDITING );
		String mwc = req.getParameter( RequestAttribute.CREDENTIAL_ATT_NAME );
		boolean isMorelloRequest = mscPara != null || mscAttrib != null || mwc != null;
		
		if ( isMorelloRequest ) {
			req.getSession().setAttribute( RequestAttribute.IS_MORELLO_REQUEST, true );
		}
		else {
			req.getSession().setAttribute( RequestAttribute.IS_MORELLO_REQUEST, false );
		}

		// If this is a staging server, user must be authenticated
		if ( AuthenticationGuard.isAuthRequiredForStaging( getServletContext(), req, res ) ) {
			return;
		}

		super.service( req, res );
	}

	@Override
	public String getInitParameter( String name ) {
		String param = super.getInitParameter( name );

		if ( param == null ) {
			param = getApplicationProperty( name );
		}

		return param;
	}

	protected String getApplicationProperty( String key ) {
		String result = null;

		getApplicationProperties();

		if ( properties == null ) {
			LOG.warn( "Failed to obtain Spring Application Properties, falling back to Servlet Context" );
			result = getServletContext().getInitParameter( key );
		}
		else {
			result = properties.get( key );
		}

		return result;
	}

	private void getApplicationProperties() {
		if ( properties == null ) {
			LOG.warn( "Obtaining Spring Application Properties" );
			properties = ApplicationProperties.getProperties();
		}
	}

	@Override
	protected void unauthorized( HttpServletRequest req, HttpServletResponse res, AuthorizationException excep ) throws java.io.IOException {
		try {
			RequestDispatcher d = req.getRequestDispatcher( "/notauthorized" );
			d.forward( req, res );
		}
		catch ( Exception e ) {
			LOG.error( "Failed to forward unauthorized request to ErrorHandlerServlet", e );
		}
	}

	Properties prop = new Properties();

	@Override
	protected Mediasurface createMediasurfaceObject( String mediasurfaceUrl, String serverName, int serverPort, String factoryClass )
			throws InitException, ConnectionException {

		Mediasurface ms = super.createMediasurfaceObject( mediasurfaceUrl, serverName, serverPort, factoryClass );
		getServletContext().setAttribute( RequestAttribute.MEDIASURFACE, ms );
		return ms;
	}

	@Override
	protected void notFound( HttpServletRequest req, HttpServletResponse res, String url ) throws IOException {
		try {
			if ( url.contains( "/content/" ) || url.contains( "/globalcontent/" ) ) {
				LOG.debug( "content store item not found ... not forwarding to error page" );
				return;
			}
			if ( url.endsWith( "/notfound" ) ) {
				super.notFound( req, res, url );
			}
			else {
				RequestDispatcher d = req.getRequestDispatcher( "/notfound" );
				res.setStatus( HttpServletResponse.SC_NOT_FOUND );
				d.forward( req, res );
			}

		}
		catch ( Exception e ) {
			LOG.error( "Failed to forward notfound request to ErrorHandlerServlet", e );
		}
	}
}
