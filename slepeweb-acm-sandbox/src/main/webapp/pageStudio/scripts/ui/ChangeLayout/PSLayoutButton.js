var PSLayoutButton = Class.create(PSButton, {
	initialize: function($super, params) {
	 $super(params);
	 if (params.memo.siteLayout)
   {
    this.siteLayout = true;
    this.element.addClassName(PSChangeLayoutTool.CLASS.SITE_LAYOUT);
   }
   else
   {
    this.siteLayout = false;
   }
	},
	
	addThumbnail: function(html) {
		if(!this.thumbnail)
		{
		  this.thumbnail = new Element("div", { "className": "ps_layout_thumbnail" });
		}
		
		this.thumbnail.update(html); //clear;		
		
		this.labelSpan.insert({ "before": this.thumbnail });
		
		this.thumbnail.select("." + PageStudio.CLASS.CELL).each(this.processCell.bind(this));
		
		this.listeners.onThumbnailMouseEnter = this.onThumbnailMouseEnter.bindAsEventListener(this);
		this.listeners.onThumbnailMouseLeave = this.onThumbnailMouseLeave.bindAsEventListener(this);
		
		this.element.observe("mouseenter", this.listeners.onThumbnailMouseEnter);
		this.element.observe("mouseleave", this.listeners.onThumbnailMouseLeave);
		
	},
	
	setLabel: function(text)
	{
		this.label.text = text;
		this.labelSpan.update(text);
	},
	
	hide: function($super)
	{
		$super();
		this.deselect();
	},
	
	onThumbnailMouseEnter: function(e) {
		this.expandThumbnail();
	},
	
	onThumbnailMouseLeave: function(e) {
		this.contractThumbnail();
	},
	
	expandThumbnail: function() {
		this.element.addClassName("expanded");
		if(this.thumbnail.viewportOffset().top + this.thumbnail.getHeight() > this.element.viewportOffset().top + this.element.getHeight()) {
		  this.thumbnail.addClassName("overflow");				
		}
	},
	
	contractThumbnail: function() {
		this.thumbnail.removeClassName("overflow");
		this.element.removeClassName("expanded");		
	},
	
	processCell: function(cell) {
		cell.removeClassName(PageStudio.CLASS.CELL);
		cell.addClassName("ps_thumbnail_cell");
		var oldWidth = this.scaleCellWidth(cell);
		PSToolTipHelper.add(cell, oldWidth, true);
		this.displayCellWidth(cell, oldWidth);
	},
		
	scaleCellWidth: function(cell) {
		var width = cell.getStyle("width");
		if (width.endsWith("px")) {
			// magic numbers :)
			//600 is an example area width since we don't know which area we are going in
			//192 is the width of the thumbnail (can't calculate it while the dialog is hidden);
			//(1 / 600) * 192 = 0.32;
			var newWidth = Math.round(parseInt(width) * 0.32)+"px"; 
			cell.setStyle({"width": newWidth});
		}
		return width;
	},
	
	displayCellWidth: function(cell, width) {
		cell.update(width);
	}
});