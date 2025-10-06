class MenuHandler {
	constructor() {
		this.submenusActive = false;
		this.debugFlag = false;
	}
	
	deactivateAllSubmenus(msg) {
		this.deactivateSubmenus([1, 2], msg)
	}
	
	deactivateSubmenus(levels, msg) {
		let s = ' Hiding all ';
		for (let i = 0; i < levels.length; i++) {
			s += 'level-' + levels[i] + ', ';
		}
		s = s.substr(0, s.length - 1);
		s += ' menu(s)';
		
		this.debug(msg ? msg : '', s);
		
		for (let i = 0; i < levels.length; i++) {
			this.deactivate($("div.submenu-" + levels[i]));
		}
	}
	
	activate(ele$, msg) {
		this.debug(msg ? msg : '');
		ele$.addClass('active');
		this.submenusActive = true;
	}
		
	deactivate(ele$, msg) {
		this.debug(msg ? msg : '');
		ele$.removeClass('active');
	}
	
	debug(...msg) {
		if (this.debugFlag) {
			let s = '';
			for (let part of msg) {
				s += ' ' + part;
			}
			
			if (s.trim().length > 0) {
				console.log(s);
			}
		}
	}
		
};

let _menu = new MenuHandler();

let userLogoutBehaviour = function(action) {
	$(`div#user-menu li#${action}-link`).click(function() {
		_site.support.ajax('GET', `/rest/${action}/${_site.siteId}`, {dataType: 'json', mimeType: 'application/json'}, function(resp) {
			if (! resp.error) {
				window.location = `${resp.data}`;
			}
		})
	})
}

$(function() {
	// Mouse pointer is over a top-level menu option
	$("div.navbar a.toplevel").on('mouseenter', function(e) {
		e.stopPropagation();
		let a$ = $(this);
		_menu.deactivateAllSubmenus('Mouse entered level-0 menu');
		_menu.activate(a$.next(), '   Activating corresponding level-1 menu');
	});
	
	// Mouse pointer is over a level-1 menu option.
	// Open the corresponding level-2 submenu
	$("div.submenu-1 > div > a").on('mouseenter', function(e) {
		e.stopPropagation();
		let a$ = $(this);
		_menu.deactivateSubmenus([2], 'Mouse entered level-1 menu');
		_menu.activate(a$.next(), '   Activating corresponding level-2 menu');
	});
	
	/*
	$("div.submenu-2").on('mouseenter', function(e) {
		e.stopPropagation();
	});
	*/
	
	$("div.main").on('mouseenter', function(e) {
		_menu.deactivateAllSubmenus('Moused over main page area');
		$('div#user-menu').addClass('hidden')		
	});
	
	$('span#user-icon').click(function() {
		$('div#user-menu').removeClass('hidden')
	})
	
	userLogoutBehaviour('logout');
	userLogoutBehaviour('superlogout');
});