package com.slepeweb.sandbox.acm.mvc;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.mediasurface.client.Mediasurface;
import com.mediasurface.datatypes.LoginOptions;
import com.mediasurface.datatypes.SecurityContextHandle;
import com.slepeweb.sandbox.acm.constants.RequestAttribute;

public class AuthenticationGuard
{
	private static Logger LOG = Logger.getLogger( AuthenticationGuard.class );
	public static boolean isAuthRequiredForStaging( ServletContext sctx, HttpServletRequest req, HttpServletResponse resp ) throws IOException
  {
    // If this is a staging server, then user must be authenticated, and ctx must be in admin mode
    Boolean isLiveServer = ( Boolean ) sctx.getAttribute( RequestAttribute.IS_LIVE_SERVER_ATT_NAME );
    
    if ( isLiveServer != null && ! isLiveServer )
    {
    	// By-pass requests for .wsdl
    	// Probably better to specify path /wsdl/ as a bypass stem, but hey!
    	if ( req.getRequestURI().endsWith( ".wsdl" ) || req.getRequestURI().endsWith( ".xsd" ) || req.getRequestURI().startsWith( "/static/" ) || req.getRequestURI().startsWith( "/lofi/" ))
    	{
    		return false;
    	}
    	
    	// If the user is already authenticated, there'll be SecurityContextHandle object in his session. Is it there?
	    Mediasurface ms = ( Mediasurface ) sctx.getAttribute( "ms" );	    
	    SecurityContextHandle ctx = ( SecurityContextHandle ) req.getSession().getAttribute( "ctx" );
	    String path = req.getRequestURI();
	    
	    if ( ctx == null )
	    {
	    	// Ok, no SecurityContextHandle object found. Do we have an Authorization header so that the
	    	// MediasurfaceControllerServlet can create the SecurityContextHandle object?
	  		String authorization = req.getHeader( "Authorization" );
	  		
	  		if ( authorization == null )
	  		{
		    	LOG.debug( path + ": visitor is NOT authenticated (staging server); authorization header is missing" );
		    	setAuthorizationHeader( req, resp );
					return true;
	  		}
	  		else
	  		{
		    	LOG.debug( path + ": visitor is NOT authenticated (staging server); authorization header exists" );
		    	ctx = loginAdminMode( req, ms );
		    	
		    	if ( ctx == null )
		    	{
			    	LOG.debug( path + ": visitor failed to authenticate (staging server)" );
			    	setAuthorizationHeader( req, resp );
						return true;
		    	}
	  		}
	    }
    }
    
    return false;
  }

  @SuppressWarnings("deprecation")
	private static SecurityContextHandle loginAdminMode( HttpServletRequest req, Mediasurface ms )
	{
		// Get details.
		String authorization = req.getHeader( "Authorization" );

		// See if anything there.
		if ( authorization == null )
		{
			// Not even attempted.
			return null;
		}

		// Check authentication method is Basic.
		final String BASIC_PREFIX = "Basic ";
		if ( ! authorization.startsWith( BASIC_PREFIX ) )
		{
			// Don't understand authentication method - will not do.
			return null;
		}

		// Handle basic authentication.
		String basicCookie = authorization.substring( BASIC_PREFIX.length() );
		String useridPassword = new String( com.mediasurface.general.Base64.decode( basicCookie ), 0 );

		// It is possible that this decoded string will have a space character at the beginning.
		// This is due to a workaround needed for the IE control used by Morello which has difficulty
		// correctly demarcating HTTP headers.
		// Strip whitespace off.
		useridPassword = useridPassword.trim();

		// Split userid:password
		int colon = useridPassword.indexOf( ':' );

		if ( colon == - 1 || colon == 0 || colon == useridPassword.length() - 1 )
		{
			// We need a username and a password - ignore.
			return null;
		}

		String userid = useridPassword.substring( 0, colon );
		String password = useridPassword.substring( colon + 1 );

		// Actually login and store in session.
		try
		{
			LoginOptions loginOptions = new LoginOptions();
			loginOptions.setAdminMode(true);
			SecurityContextHandle ctx = ms.secureLogin( userid, password, loginOptions );
			
			req.getSession().setAttribute( RequestAttribute.SECURITY_CONTEXT, ctx );
			LOG.debug( userid + " logged-in (staging server), in admin mode [" + req.getRequestURI() + "]" );
			return ctx;
		}
		catch ( Exception exc )
		{
			LOG.error( "Login failure [" + userid + "]", exc );
			return null;
		}
	}

	private static void setAuthorizationHeader( HttpServletRequest req, HttpServletResponse resp ) throws IOException
  {
		resp.setHeader( "WWW-Authenticate", "Basic realm=\"Mediasurface\"" );
		resp.sendError( HttpServletResponse.SC_UNAUTHORIZED ); 
		req.setAttribute( RequestAttribute.FLAG_ATT_NAME, new Boolean( true ) );
  }
}
