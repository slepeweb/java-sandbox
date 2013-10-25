package com.slepeweb.sandbox.acm.navcache;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.mediasurface.client.IFieldDef;
import com.mediasurface.client.IItem;
import com.slepeweb.sandbox.acm.navcache.CacheableProperties.CacheablePropertyRule;
import com.slepeweb.sandbox.acm.navcache.CacheableProperties.FieldDefinition;

/**
 * This class extends NavigationLinkAbstract. Customer specific functionality for each IItem should go in this class.
 * 
 * @author Amit Viroja
 */
public class CachedItem extends CachedItemBase {

	private static Logger LOG = Logger.getLogger( CachedItem.class );

	protected long lastRefreshedThumbnail;
	protected CachedItemInline thumbnail;
	protected Map<String, CachedItemInline> inlines;
	private String market, language;
	private boolean hideFromErrorPage = false;
	private Map<String, Object> extras;

	public CachedItem( IItem item ) {
		super( item );

		String s = ifFieldExists( item, "hidefromerrorpage" );
		if ( s != null && s.equalsIgnoreCase( "yes" ) ) {
			this.hideFromErrorPage = true;
		}

		// Store cacheable field values
		cacheProperties( item );

		// do type specific stuff
		doTypeSpecificOperations( item );

	}

	/**
	 * Loop through all fields on this item, test each one for cacheability, and cache it if appropriate.
	 * 
	 * @param item
	 */
	private void cacheProperties( IItem item ) {

		// Identifuy all the fields on this item, and their data types
		Map<String, Integer> fieldMap = new HashMap<String, Integer>( this.fieldNames.size() );

		try {
			IFieldDef[] fieldDefs = item.getType().getFieldDefinitions();
			for ( IFieldDef def : fieldDefs ) {
				fieldMap.put( def.getVariableName(), def.getFieldType() );
			}
		}
		catch ( Exception e ) {
			LOG.error( String.format( "Failed to map item fields [%s]", getUrl() ), e );
			return;
		}

		// For each field on the item, test whether it needs to be cached as a property,
		// and cache if required.
		Integer fieldType;
		Object value;

		for ( String fieldName : this.fieldNames ) {
			fieldType = fieldMap.get( fieldName );
			value = getFieldValue( item, fieldName, fieldType );

			if ( CacheableProperties.isCacheable( fieldName, getItemTypeName(), value ) ) {
				// The field value may need 'handling'
				setCacheableProperty( fieldName, value );
			}
		}

		// Finally, cache certain properties regardless of whether the item has matching fields
		for ( FieldDefinition defn : CacheableProperties.getPropertiesAlwaysCached() ) {
			value = getFieldValue( item, defn.getName(), defn.getType() );
			setCacheableProperty( defn.getName(), value );
		}
	}

	private void setCacheableProperty( String property, Object value ) {
		CacheablePropertyRule rules = CacheableProperties.getRules( property );

		if ( rules != null ) {
			setProperty( property, rules.apply( value, getUrl() ) );
		}
		else {
			setProperty( property, value );
		}
	}

	private Object getFieldValue( IItem item, String fieldName, Integer fieldType ) {
		if ( fieldType == IFieldDef.INT_FIELD_TYPE ) {
			return ifIntFieldExists( item, fieldName );
		}
		else if ( fieldType == IFieldDef.DATE_FIELD_TYPE ) {
			return ifDateFieldExists( item, fieldName );
		}
		else {
			return ifFieldExists( item, fieldName );
		}
	}

	public void doTypeSpecificOperations( IItem item ) {
		//LOG.debug( "doing type specific operations" );
	}

	public void setProperty( String key, Object value ) {
		if ( value != null ) {
			if ( this.extras == null ) {
				this.extras = new HashMap<String, Object>();
			}
			this.extras.put( key, value );
		}
	}

	public boolean isHideFromErrorPage() {
		return hideFromErrorPage;
	}

	public Object getProperty( String key ) {
		if ( this.extras == null ) {
			return null;
		}
		return this.extras.get( key );
	}

	public Object getProperties() {
		return this.extras;
	}

	public CachedItemInline getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail( CachedItemInline thumbnail ) {
		this.thumbnail = thumbnail;
	}

	public void setInlines( Map<String, CachedItemInline> inlines ) {
		this.inlines = inlines;
	}

	public Map<String, CachedItemInline> getInlines() {
		return this.inlines;
	}

	public CachedItemInline getInline( String purpose ) {
		if ( this.inlines != null ) {
			return this.inlines.get( purpose );
		}
		else {
			return null;
		}

	}

	public void setLastRefreshedThumbnail() {
		this.lastRefreshedThumbnail = System.currentTimeMillis();
	}

	public long getLastRefreshedThumbnail() {
		return this.lastRefreshedThumbnail;
	}

	public String getMarket() {
		return market;
	}

	public void setMarket( String market ) {
		this.market = market;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage( String language ) {
		this.language = language;
	}
}
