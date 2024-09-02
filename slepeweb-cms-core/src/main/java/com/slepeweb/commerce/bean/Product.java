package com.slepeweb.commerce.bean;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.slepeweb.cms.bean.Item;
import com.slepeweb.cms.bean.LinkFilter;
import com.slepeweb.cms.constant.ItemTypeName;
import com.slepeweb.cms.except.ResourceException;
import com.slepeweb.cms.utils.CmsUtil;

public class Product extends Item {
	private static final long serialVersionUID = 1L;
	public static final String HIFI_EXT = "-hifi";
	public static final String VARIANTS_FOLDER_SIMPLENAME = "v";
	public static NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance();
	//private static Logger LOG = Logger.getLogger(Product.class);
	
	private String partNum;
	private Long stock, price;
	private Long alphaAxisId, betaAxisId;
	private List<Variant> variants;
	private List<Item> hifiImages, variantImages, carouselImages;
	private Item variantImageFolder;
		
	public String getHifiImagePath(String basePath) {
		resolveImages();
		String baseSimpleName = CmsUtil.getSimplename(basePath);
		for (Item img : getHifiImages()) {
			if (img.getSimpleName().equals(baseSimpleName + HIFI_EXT)) {
				return img.getPath();
			}
		}

		return null;
	}
	
	@Override
	public boolean isProduct() {
		return true;
	}
	
	@Override
	public Product setOrigId(Long origId) {
		super.setOrigId(origId);
		return this;
	}
	
	@Override
	public void assimilate(Object obj) {
		super.assimilate(obj);
		assimilateProduct(obj);
	}
	
	public void assimilateProduct(Object obj) {
		if (obj instanceof Product) {
			Product p = (Product) obj;
			setPartNum(p.getPartNum());
			setStock(p.getStock());
			setPrice(p.getPrice());
			setAlphaAxisId(p.getAlphaAxisId());
			setBetaAxisId(p.getBetaAxisId());
		}
	}
	
	@Override
	public boolean isDefined4Insert() {
		return super.isDefined4Insert() && StringUtils.isNotBlank(getPartNum());
	}
	
	@Override
	public String toString() {
		return String.format("Product '%s' (%d @ %f.2)", getPartNum(), getStock(), getPriceInPounds());
	}
	
	@Override
	public Product save() throws ResourceException {
		return getProductService().save(this);
	}
	
	// This deletes a specific version of an item, and NOT all versions
	@Override
	public void delete() {
		getProductService().deleteAllVersions(getOrigId());
	}
	
	public Product copy() {
		// TODO: These two lines are short-term hacks - needs further investigation.
		Integer qualifier = 1; 
		String partNum = getPartNum() + "-copy-" + String.valueOf(qualifier);
		
		try {
			return getProductService().copy(this, getName() + "-COPY", "", partNum, qualifier);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return null;
	}
	
	public Item getOrderItemThumbnail(String qualifier) {
		resolveImages();
		Item bestMatch = null;
		
		if (getAlphaAxis() != null && getBetaAxis() != null) {
			for (Item i : getVariantImages()) {
				if (i.getSimpleName().equals(qualifier)) {					
					return i;
				}
				else if (qualifier.contains(i.getSimpleName())) {					
					bestMatch = i;
				}					
			}
			
			return bestMatch;
		}
		else if (getAlphaAxis() != null) {
			for (Item i : getVariantImages()) {
				if (i.getSimpleName().equals(qualifier)) {
					return i;
				}
			}
		}
		else if (getCarouselImages().size() > 0) {
			return getCarouselImages().get(0);
		}
		
		return null;
	}
	
	/*
	 * Images are expected to conform to the following structure in the content store:
	 * 
	 * <root container> (Content Folder)
	 * 		|- image1
	 * 		|- image1-hifi
	 * 		|- image2
	 * 		|- image2-hifi
	 * 		|- (etc)
	 * 		|- v (Content Folder)
	 * 			|- image1-axisA[-axisB]
	 * 			|- image2-axisA[-axisB]
	 * 			|- (etc)
	 * 
	 * NOTES:
	 * 1) images in v folder can be shortcut to one level below the root container
	 * 2) hifi images matching variants must be one level below the container, and must
	 *    have '-hifi' appended to their simplenames
	 * 3) hifi images are optional
	 * 4) If product does not have variants, as defined by Product item, then the v
	 *    folder is ignored when this method executes.
	 * 5) Image names for variants should be comprised from one or more axis names,
	 *    eg.'brown', or 'brown-wide', etc.
	 */
	private void resolveImages() {
		if (this.carouselImages == null && this.hifiImages == null && this.variantImages == null) {
			
			this.carouselImages = new ArrayList<Item>();
			this.hifiImages = new ArrayList<Item>();
			this.variantImages = new ArrayList<Item>();
			
			Item mainImage = getFirstInlineImage();
			if (mainImage != null) {
				Item container = mainImage.getOrthogonalParent();
				
				// This image might be in a variants folder.
				if (container != null && container.getSimpleName().equals(VARIANTS_FOLDER_SIMPLENAME)) {
					this.variantImageFolder = container;
					// Climb one level higher
					container = container.getOrthogonalParent();
				}
				
				if (container != null) {
					
					// Carousel & Hifi images
					LinkFilter imageFilter = new LinkFilter().setItemTypes(new String[] {
							ItemTypeName.IMAGE_JPG,
							ItemTypeName.IMAGE_PNG,
							ItemTypeName.IMAGE_GIF
					});
					
					for (Item img : imageFilter.filterItems(container.getBindings())) {
						if (img.getSimpleName().endsWith(HIFI_EXT)) {
							this.hifiImages.add(img);
						}
						else {
							this.carouselImages.add(img);
						}
					}
					
					// Variant images
					if (getAlphaAxisId() > 0L || getBetaAxisId() > 0L) {
						LinkFilter folderFilter = new LinkFilter().
								setSimpleNamePattern(VARIANTS_FOLDER_SIMPLENAME).
								setItemType(ItemTypeName.CONTENT_FOLDER);								
						
						if (this.variantImageFolder == null) {
							this.variantImageFolder = folderFilter.filterFirstItem(container.getBindings());
						}
						
						if (this.variantImageFolder != null) {
							this.variantImages = imageFilter.filterItems(this.variantImageFolder.getBindings());
						}
					}
				}
			}
		}
	}
	

	public List<Item> getHifiImages() {
		if (this.hifiImages == null) {
			resolveImages();
		}
		return this.hifiImages;
	}


	public List<Item> getVariantImages() {
		if (this.variantImages == null) {
			resolveImages();
		}
		return this.variantImages;
	}

	public List<Item> getCarouselImages() {
		if (this.carouselImages == null) {
			resolveImages();
		}
		return this.carouselImages;
	}
	
	public Item getMatchingHifiImage(Item testImg) {
		if (getHifiImages() != null) {
			for (Item i : getHifiImages()) {
				if (i.getSimpleName().equals(testImg.getSimpleName() + HIFI_EXT)) {
					return i;
				}
			}
		}
		return null;
	}
	
	public String getPartNum() {
		return partNum;
	}

	public Product setPartNum(String partNum) {
		this.partNum = partNum;
		return this;
	}

	public Long getStock() {
		return this.stock == null ? 0L : this.stock;
	}
	
	public Long getStockForVariants() {
		if (getVariants() != null && getVariants().size() > 0) {
			Long count = 0L;
			for (Variant v : getVariants()) {
				count += v.getStock();
			}
			return count;
		}
		return 0L;
	}

	public Product setStock(Long stock) {
		this.stock = stock;
		return this;
	}

	public Long getPrice() {
		return price;
	}

	public float getPriceInPounds() {
		return this.price != null ? this.price / 100F : -1.0F;
	}

	public String getPriceInPoundsAsString() {
		return CURRENCY_FORMAT.format(getPriceInPounds());
	}

	public Product setPrice(Long price) {
		this.price = price;
		return this;
	}

	public Long getAlphaAxisId() {
		return this.alphaAxisId == null ? -1L : this.alphaAxisId;
	}

	public Axis getBetaAxis() {
		if (this.betaAxisId != null) {
			return getAxisService().get(this.betaAxisId);
		}
		return null;
	}

	public Product setAlphaAxisId(Long alphaAxis) {
		this.alphaAxisId = alphaAxis;
		return this;
	}

	public Long getBetaAxisId() {
		return this.betaAxisId == null ? -1L : this.betaAxisId;
	}

	public Axis getAlphaAxis() {
		if (this.alphaAxisId != null) {
			return getAxisService().get(this.alphaAxisId);
		}
		return null;
	}

	public Product setBetaAxisId(Long betaAxis) {
		this.betaAxisId = betaAxis;
		return this;
	}

	public Product setVariants(List<Variant> variants) {
		this.variants = variants;
		return this;
	}
	
	public boolean isHasVariants() {
		return getVariantService().count(getOrigId()).longValue() > 0;
	}

	public List<Variant> getVariants() {
		if (this.variants == null) {
			this.variants = getVariantService().getMany(getOrigId(), null, null);
		}
		return this.variants;
	}
	
	public Variant getVariant(String qualifier) {
		return getVariantService().get(getOrigId(), qualifier);
	}
	
	public AxisValueSelector getAlphaAxisValues() {
		return getVariantService().getAlphaAxisSelector(getOrigId(), getAlphaAxisId());
	}

	public Item getVariantImageFolder() {
		return variantImageFolder;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((alphaAxisId == null) ? 0 : alphaAxisId.hashCode());
		result = prime * result + ((betaAxisId == null) ? 0 : betaAxisId.hashCode());
		result = prime * result + ((partNum == null) ? 0 : partNum.hashCode());
		result = prime * result + ((price == null) ? 0 : price.hashCode());
		result = prime * result + ((stock == null) ? 0 : stock.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		
		if (!super.equals(obj))
			return false;
		
		if (! equalsProduct(obj))
			return false;

		return true;
	}
	
	public boolean equalsProduct(Object obj) {
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		Product other = (Product) obj;
		if (alphaAxisId == null) {
			if (other.alphaAxisId != null)
				return false;
		} else if (!alphaAxisId.equals(other.alphaAxisId))
			return false;
		if (betaAxisId == null) {
			if (other.betaAxisId != null)
				return false;
		} else if (!betaAxisId.equals(other.betaAxisId))
			return false;
		if (partNum == null) {
			if (other.partNum != null)
				return false;
		} else if (!partNum.equals(other.partNum))
			return false;
		if (price == null) {
			if (other.price != null)
				return false;
		} else if (!price.equals(other.price))
			return false;
		if (stock == null) {
			if (other.stock != null)
				return false;
		} else if (!stock.equals(other.stock))
			return false;
		return true;
	}
}
