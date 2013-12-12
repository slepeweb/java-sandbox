var PSModalDialog = Class.create(PSDialog, {	
	show: function($super) {
		$super();
		this.blocker = new Element("div", { "className": PSModalDialog.CLASS.BLOCKER });
		this.element.insert(this.blocker);
		this.blocker.observe("mousedown", this.onBlockerClicked.bindAsEventListener(this));
	},
	
	hide: function($super) {
		if(this.blocker) {
			this.blocker.stopObserving();
			if(this.blocker.parentNode) this.blocker.remove();
		}
		$super();
	},
	
	highlight: function() {
		//make the dialog flash
		new PeriodicalExecuter(function(pe) {
			pe.iter = pe.iter != null ? pe.iter+1 : 0;
			this.element.toggleClassName(PSDialog.CLASS.FOCUSED);
			if(pe.iter == 5) pe.stop();
		}.bind(this), 0.1);
	},
	
	onBlockerClicked: function(e) {
		this.highlight();
		e.stop();
	}
});

PSModalDialog.CLASS = {
	BLOCKER: "ps_blocker"
};

var PSErrorDialog = Class.create(PSModalDialog, {
	initParams: function($super, params) {
		this.report = params.report;
		this.message = params.message;
		$super(params);
	},
	
	createElement: function($super, parent) {
		$super(parent);
		this.element.addClassName("ps_error_dialog");
		this.toolEle = new Element("div", { "class": "ps_tool" }); 
		this.errorContainer = new Element("div", { "class": "ps_error_dialog_container" });
		this.message = new Element("p", { "class": "ps_error_dialog_message" }).update(this.message);
		this.type = new Element("p", { "class": "ps_error_dialog_type" }).insert(new Element("span").update(PageStudio.locale.get("Reason"))).insert(": " + PageStudio.getExceptionTypeName(this.report.exceptionType));
		this.details = new Element("pre", { "class": "ps_error_dialog_details" }).insert(new Element("span").update(PageStudio.locale.get("Details"))).insert(": " + decodeURIComponent(this.report.message).escapeHTML());
		this.body.insert(this.toolEle.insert(this.errorContainer.insert(this.message).insert(this.type).insert(this.details)));
		PSUnselectableHelper.makeSelectable(this.element);
		new PSResizableHelper(this.element, { handle: "right bottom", grip: true });
	},
	
	show: function($super) {
		$super();
		this.element.setStyle({ 
			"height": this.errorContainer.getHeight() + PSErrorDialog.DIALOG_PADDING + "px"
		});
	},
	
	onCloseClicked: function($super, e) {
		$super(e);
		this.remove();
	}
});

PSErrorDialog.DIALOG_PADDING = 56;