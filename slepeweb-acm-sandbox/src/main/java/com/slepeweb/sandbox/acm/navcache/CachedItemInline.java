package com.slepeweb.sandbox.acm.navcache;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

/*
 * IMPORTANT: All image simple names are expected to follow the same pattern, providing image size.
 *            Otherwise, the width and height of the image will not be known, and it will not be
 *            possible to calculate urls for size variants.
 */

@Component
public class CachedItemInline implements Cloneable, Serializable {
	private static final long serialVersionUID = 1L;

	private static Logger LOG = Logger.getLogger( CachedItemInline.class );

	private String type, purposeStr, url, path, title, simpleName;
	private ImagePurpose purpose;
	private int id, originalItemId;

	public enum ImagePurpose {
		HERO, SUBNAV, BACKGROUND, FLYOUT, THUMBNAIL, GALLERY, COLOUR, PERFORMANCE, COMFORT, ACCELERATION, SPEED, FUELCONSUMPTION, ECONOMY, MAIN, ENGINE, MAGNIFIER, PANNED, ICON, SIDEBAR, CAPACITY, LEGROOM, DOWNLOAD, BODYSTYLE;
	}

	public CachedItemInline() {
	}

	public CachedItemInline( CachedItem navLink ) {
		this( navLink, null, null );
	}

	public CachedItemInline( CachedItem navLink, String market, String language ) {
		this.id = navLink.getKey();
		this.simpleName = navLink.getSimpleName();
		this.url = navLink.getUrl();
		this.path = navLink.getPath();
		this.type = navLink.getItemTypeName();
		this.originalItemId = Integer.valueOf( navLink.getOriginalItemId() );

		this.purposeStr = ( String ) navLink.getProperty( "purpose" );
		if ( this.purposeStr != null ) {
			this.purpose = ImagePurpose.valueOf( this.purposeStr.toUpperCase() );
		}

		this.title = navLink.getTitle();
	}

	@SuppressWarnings("unused")
	private String removeLastUrlComponent( String url ) {
		String s = url.endsWith( "/" ) ? url.substring( 0, url.length() - 1 ) : url;
		int cursor = s.lastIndexOf( "/" );

		if ( cursor > - 1 ) {
			return s.substring( 0, cursor + 1 );
		}

		LOG.warn( "Failed to determine parent path/url" );
		return "/";
	}

	public int getOriginalItemId() {
		return originalItemId;
	}

	public void setOriginalItemId( int originalItemId ) {
		this.originalItemId = originalItemId;
	}

	public String getPurposeStr() {
		return this.purposeStr;
	}

	public ImagePurpose getPurpose() {
		return this.purpose;
	}

	public String getUrl() {
		return this.url;
	}

	public String getPath() {
		return this.path;
	}

	public void setPath( String s ) {
		this.path = s;
	}

	public void setUrl( String s ) {
		this.url = s;
	}

	public String getAltText() {
		return getTitle();
	}

	public String getTitle() {
		return this.title;
	}

	public String getType() {
		return this.type;
	}

	public int getId() {
		return this.id;
	}

	public String getSimpleName() {
		return this.simpleName;
	}
}
