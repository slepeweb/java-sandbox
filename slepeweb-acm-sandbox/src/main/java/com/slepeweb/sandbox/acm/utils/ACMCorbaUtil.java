package com.slepeweb.sandbox.acm.utils;

import com.sun.corba.se.impl.javax.rmi.CORBA.Util;

/**
 * The default implementation of this class returns a 14k string for the
 * codebase, which seems to get sent with every CORBA request. This string is
 * made up of the classpath amongst other things. The server end has no need for
 * this and so we send an empty string instead. This has the effect of reducing
 * data sent by 80% and makes the client more responsive. A command line
 * property definition is required to use this class:
 *
 * -Djavax.rmi.CORBA.UtilClass=com.jdxwww.sdl.utils.ACMCorbaUtil
 */
public class ACMCorbaUtil extends Util
{
	@Override
	@SuppressWarnings("rawtypes")
	public String getCodebase( Class paramClass )
	{
		return "";
	}
}