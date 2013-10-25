package com.slepeweb.sandbox.acm.navcache;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.mediasurface.client.IItem;
import com.mediasurface.client.ILocale;
import com.mediasurface.client.IType;
import com.mediasurface.general.UrlHolder;
import com.slepeweb.sandbox.acm.constants.FieldName;

/**
 * This class holds minimum information required for each IItem object in ACM. To add project specific features extend
 * this class.
 * 
 * @author Amit Viroja
 */
public abstract class CachedItemBase {
	private static Logger LOG = Logger.getLogger( CachedItemBase.class );
	private static final int VERSION_FACTOR = 10000;

	protected long lastAccessed, changeFrequency;
	protected String linkText, linkPath, linkUrl, title, teaser, itemTypeName, collection;
	protected String fullName, simpleName, status;
	protected String itemId, originaltemId;
	protected Date datePublished, updateDate;
	protected int revision;
	protected boolean disabled, isEmbargo, highlighted;
	protected int maxWordsInTeaser;
	protected boolean isSet;
	protected boolean visibleOnMainNav, childrenVisibleOnMainNav, isLeaf, isBinary;
	protected Locale locale;
	protected String localeString;
	protected String hostKey;

	// TODO: Review
	protected Set<String> fieldNames;

	public abstract void doTypeSpecificOperations( IItem navItem );

	public abstract Object getProperty( String key );

	private void init() {
		this.itemTypeName = this.linkText = this.title = this.teaser = "";
		this.linkPath = this.linkUrl = null;
		this.maxWordsInTeaser = 0;
		this.revision = 0;
		this.lastAccessed = 0;
		this.visibleOnMainNav = this.childrenVisibleOnMainNav = true;
		this.fullName = this.simpleName = this.status = this.itemId = this.originaltemId = "";
	}

	public CachedItemBase( String path, String displayText, String teaser, String binaryItemType ) {
		this( path, displayText, teaser );
		this.itemTypeName = binaryItemType;
	}

	public CachedItemBase( String path, String displayText, String teaser ) {
		init();
		this.linkUrl = this.linkPath = path; // this constructor is for an
		// external link so path and url
		// should return the same thing.
		this.title = this.linkText = displayText;
		this.teaser = teaser;
		this.isSet = true;
	}

	public CachedItemBase( IItem iItem ) {
		this( iItem, false );
	}

	public CachedItemBase( IItem iItem, boolean disable ) {
		this( iItem, false, false );
	}

	public CachedItemBase( IItem iItem, boolean disable, boolean highlight ) {
		init();

		boolean ok = true;
		String[] fieldNamesArray = null;

		try {
			fieldNamesArray = iItem.getFieldNames();
			IType navItemType = iItem.getType();

			this.itemTypeName = navItemType.getName();
			this.changeFrequency = navItemType.getCacheTime();
			this.collection = iItem.getCollection().getName();
			this.isLeaf = ! navItemType.isLinkable();
			this.isBinary = navItemType.isBinary();
			this.itemId = iItem.getKey().getKey();
			this.originaltemId = iItem.getOriginal().getKey().getKey();
			this.linkPath = iItem.getPath();
			this.linkUrl = stripVersionFromUrl( iItem.getUrl() );
			this.simpleName = iItem.getSimpleName();
			this.fullName = iItem.getFullName();
			this.status = iItem.getStatus().getName();
			ILocale iLocale = iItem.getLocale();
			this.updateDate = iItem.getUpdateDate();

			this.locale = new Locale( iLocale.getIso639LanguageCode(), iLocale.getIso3166CountryCode() );
			if ( this.locale != null ) {
				this.localeString = this.locale.getISO3Language() + "_" + this.locale.getISO3Country();
			}
			else {
				this.localeString = "en_GB"; // default to UK english
			}

			hostKey = iItem.getHost().getKey().getKey();

		}
		catch ( Exception e ) {
			ok = false;
			LOG.error( "Failed to retrieve item data", e );
		}

		if ( ok ) {
			// Other field data
			fieldNames = new HashSet<String>( fieldNamesArray.length );
			for ( String s : fieldNamesArray ) {
				fieldNames.add( s );
			}

			this.title = notNull( ifFieldExists( iItem, FieldName.TITLE ) );
			this.linkText = ifFieldExists( iItem, FieldName.LINK_TEXT );

			if ( StringUtils.isBlank( this.linkText ) ) {
				this.linkText = this.title;
			}

			// Remove this fallback to fullName if not required
			if ( StringUtils.isBlank( this.linkText ) ) {
				this.linkText = this.fullName;
			}

			this.teaser = ifFieldExists( iItem, FieldName.TEASER );
			this.datePublished = ifDateFieldExists( iItem, FieldName.DATE_PUBLISHED );

			String s = ifFieldExists( iItem, FieldName.HIDE_ITEM_FROM_NAVIGATION );
			if ( s != null && s.equalsIgnoreCase( "yes" ) ) {
				this.visibleOnMainNav = false;
			}

			s = ifFieldExists( iItem, FieldName.HIDE_CHILDREN_FROM_NAVIGATION );
			if ( s != null && s.equalsIgnoreCase( "yes" ) ) {
				this.childrenVisibleOnMainNav = false;
			}

			this.revision = getRevision( iItem );

			// getBranchIndexes(iItem);

			// Link visibility and status
			this.disabled = disable;
			this.highlighted = highlight || this.disabled;
			this.isSet = true;
		}
	}

	protected String ifFieldExists( IItem item, String fieldName ) {
		if ( this.fieldNames.contains( fieldName ) ) {
			return getFieldValue( item, fieldName );
		}
		return null;
	}

	protected int ifIntFieldExists( IItem item, String fieldName ) {
		if ( this.fieldNames.contains( fieldName ) ) {
			Integer value = getIntegerFieldValue( item, fieldName );
			return value == null ? 0 : value;
		}
		return 0;
	}

	protected Date ifDateFieldExists( IItem item, String fieldName ) {
		if ( this.fieldNames.contains( fieldName ) ) {
			return getDateFieldValue( item, fieldName );
		}
		return null;
	}

	protected Boolean ifBooleanFieldExists( IItem item, String fieldName ) {
		String str = ifFieldExists( item, fieldName );
		if ( str != null ) {
			return str.trim().toLowerCase().matches( "y|yes|true" );
		}
		else {
			return null;
		}
	}

	protected Set<String> ifTaxonomyFieldExists( IItem item, String fieldName ) {
		if ( this.fieldNames.contains( fieldName ) ) {
			return getTaxonomyFieldValue( item, fieldName );
		}
		return null;
	}

	public boolean isSet() {
		return this.isSet;
	}

	public boolean isBinary() {
		return this.isBinary;
	}

	public boolean isLeaf() {
		return this.isLeaf;
	}

	public void setKey( String s ) {
		this.itemId = s;
	}

	public int getKey() {
		return Integer.parseInt( this.itemId );
	}

	/**
	 * TODO: Review This method is proxied so to return i18n nav link's item id
	 */
	public int getI18nNavLinkKey() {
		return Integer.parseInt( this.itemId );
	}

	public int getOriginalKey() {
		return Integer.parseInt( this.originaltemId );
	}

	public String getItemTypeName() {
		return this.itemTypeName;
	}

	public String getCollection() {
		return this.collection;
	}

	public String getItemId() {
		return this.itemId;
	}

	public String getOriginalItemId() {
		return this.originaltemId;
	}

	public void setTitle( String s ) {
		this.title = s;
	}

	public String getTitle() {
		return this.title;
	}

	public void setText( String s ) {
		this.linkText = s;
	}

	public String getText() {
		return this.linkText;
	}

	public void setTeaser( String s ) {
		this.teaser = s;
	}

	public String getTeaser() {
		// while using common item type, some items doesn't need teaser to be
		// set.
		// editors get away with just adding underscore.
		if ( this.teaser == null || this.teaser.equals( "_" ) ) {
			return "";
		}
		return truncate( this.teaser, this.maxWordsInTeaser );
	}

	public Locale getLocale() {
		return locale;
	}

	public String getLocaleString() {
		return localeString;
	}

	public void setPath( IItem item ) {
		try {
			this.linkPath = item.getPath();
			this.linkUrl = item.getUrl();
		}
		catch ( Exception e ) {
			LOG.error( "Error updating path/url properties", e );
		}
	}

	public String getPath() {
		return this.linkPath;
	}

	public void setPath( String s ) {
		this.linkPath = s;
	}

	public String getUrl() {
		return this.linkUrl;// this.linkPath;
	}

	public void setUrl( String s ) {
		this.linkUrl = s;
	}

	public String getBinaryShortname() {

		return "";
	}

	public Date getDatePublished() {
		return this.datePublished;
	}

	public long getChangeFrequency() {
		return this.changeFrequency;
	}

	public boolean isVisibleOnMainNav() {
		return this.visibleOnMainNav;
	}

	public boolean isChildrenVisibleOnMainNav() {
		return this.childrenVisibleOnMainNav;
	}

	public void setMaxWords( int number ) {
		this.maxWordsInTeaser = number;
	}

	public void setDatePublished( Date d ) {
		this.datePublished = d;
	}

	public void setDisable( boolean b ) {
		this.disabled = b;
	}

	public boolean isDisabled() {
		return this.disabled;
	}

	public void setHighlight( boolean b ) {
		this.highlighted = b;
	}

	public boolean isHighlighted() {
		return this.highlighted;
	}

	void setLastAccessed() {
		this.lastAccessed = new Date().getTime();
	}

	long getLastAccessed() {
		return this.lastAccessed;
	}

	public boolean isPathMatch( IItem otherItem ) {
		try {
			return linkPath.equals( otherItem.getPath() );
		}
		catch ( Exception e ) {
			LOG.error( "Failed to validate feshness by path", e );
			return false;
		}
	}

	public boolean isRevisionMatch( IItem otherItem ) {
		int otherRevision = getRevision( otherItem );
		return this.revision != - 1 && otherRevision != - 1 && otherRevision == revision;
	}

	@SuppressWarnings("unused")
	private int getRevision() {
		return this.revision;
	}

	private int getRevision( IItem item ) {
		try {
			if ( ! item.isReferenceItem() ) {
				return factorRevision( item );
			}
			else {
				return factorRevision( item.getReferredToItem() );
			}
		}
		catch ( Exception e ) {
			return - 1;
		}
	}

	private int factorRevision( IItem item ) throws Exception {
		return ( VERSION_FACTOR * item.getVersion() ) + item.getRevision();
	}

	public String getSimpleName() {
		return this.simpleName;
	}

	public String getFullName() {
		return this.fullName;
	}

	public String getStatus() {
		return this.status;
	}

	private String truncate( String s, int maxWords ) {
		String result = s;

		if ( maxWords > 0 ) {
			StringTokenizer st = new StringTokenizer( s, " \t\n\r" );
			if ( st.countTokens() > maxWords ) {
				StringBuffer sb = new StringBuffer();
				for ( int i = 0; i < maxWords; i++ ) {
					sb.append( st.nextToken() + " " );
				}
				sb.append( "..." );
				result = sb.toString();
			}
		}
		return result;
	}

	@Override
	public Object clone() {
		try {
			return super.clone();
		}
		catch ( CloneNotSupportedException e ) {
			return null;
		}
	}

	private boolean isEmbargo( Date now ) {
		if ( this.datePublished != null ) {
			return this.datePublished.after( now );
		}
		return false;
	}

	/**
	 * Removes the 'version=N' parameter from the supplied url, if present.
	 * 
	 * @param url
	 * @return
	 */
	private static String stripVersionFromUrl( String url ) {
		int cursor;

		if ( ( cursor = url.indexOf( "version=" ) ) > - 1 ) {
			// Url has a version param
			cursor = url.indexOf( "?" );
			String hostPlusPath = url.substring( 0, cursor );
			String params = url.substring( cursor + 1 );
			String[] pairs = params.split( "&" );

			if ( pairs.length == 1 ) {
				// Url has a single param - the version
				return hostPlusPath;
			}
			else {
				// Url has a multiple params - remove version
				StringBuffer buff = new StringBuffer();

				for ( int i = 0; i < pairs.length; i++ ) {
					if ( ! pairs[ i ].startsWith( "version" ) ) {
						if ( buff.length() > 0 ) {
							buff.append( "&" );
						}
						buff.append( pairs[ i ] );
					}
				}

				return hostPlusPath + "?" + buff.toString();
			}
		}
		return url;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public boolean isEmbargo() {
		return isEmbargo( new Date() );
	}

	/**
	 * Method removes view=Standard parameter
	 * 
	 * @return
	 */
	@SuppressWarnings("unused")
	private String getUrlFiltered() {
		try {
			UrlHolder h = new UrlHolder( this.linkUrl );
			if ( StringUtils.isNotBlank( h.getView() ) && h.getView().equals( "Standard" ) ) {
				h.setView( null );
				return h.toString();
			}
		}
		catch ( Exception e ) {
			LOG.error( "Malformed URL [" + this.linkUrl + "]" );
		}
		return this.linkUrl;
	}

	protected String ifFieldExists( IItem item, String fieldName, Set<String> fieldNames ) {
		if ( fieldNames.contains( fieldName ) ) {
			return getFieldValue( item, fieldName );
		}

		return null;
	}

	public String getHostKey() {
		return this.hostKey;
	}

	protected String getFieldValue( IItem item, String field ) {
		return getFieldValue( item, field, true );
	}

	protected String getFieldValue( IItem item, String field, boolean resolveLinks ) {
		if ( item != null ) {
			try {
				return item.getFieldValue( field, resolveLinks );
			}
			catch ( Exception e ) {
			}
		}

		return null;
	}

	protected Integer getIntegerFieldValue( IItem item, String field ) {
		if ( item != null ) {
			try {
				return item.getIntFieldValue( field );
			}
			catch ( Exception e ) {
			}
		}

		return null;
	}

	protected Date getDateFieldValue( IItem item, String field ) {
		if ( item != null ) {
			try {
				return item.getDateFieldValue( field );
			}
			catch ( Exception e ) {
			}
		}

		return null;
	}

	protected Set<String> getTaxonomyFieldValue( IItem item, String field ) {
		if ( item != null ) {
			String s = null;

			try {
				s = item.getFieldValue( field );
			}
			catch ( Exception e ) {
			}

			if ( s != null ) {
				/*
				 * This string will be a list of quoted sub-strings, delimited by commas, eg. "XJ", "XK". Assume that the
				 * sub-strings do NOT have commas in them too!
				 */
				String[] terms = s.split( "," );
				Set<String> set = new HashSet<String>();

				for ( String term : terms ) {
					set.add( term );
				}

				return set;
			}
		}

		return null;
	}

	protected String notNull( String s ) {
		return s == null ? "" : s;
	}

	protected Integer notNull( Integer i ) {
		return i == null ? new Integer( 0 ) : i;
	}

	protected Date notNull( Date d ) {
		return d == null ? new Date() : d;
	}

	protected boolean notNull( Boolean b ) {
		return b == null ? new Boolean( false ).booleanValue() : b.booleanValue();
	}

	@SuppressWarnings("rawtypes")
	protected HashMap notNull( HashMap h ) {
		return h == null ? new HashMap() : h;
	}

}
