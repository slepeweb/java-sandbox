$(function() {
	$("div.navbar a.toplevel").mouseover(function() {
		let a$ = $(this);
		$("div.navbar div.submenu-1").removeClass("active");
		a$.next().addClass("active");
	});
	
	$("div.submenu-1 > a").mouseover(function() {
		let a$ = $(this);
		$("div.submenu-1 div.submenu-2").removeClass("active");
		a$.next().addClass("active");
	});
	
	$("div.submenu-1, div.submenu-2").mouseleave(function() {
		let div$ = $(this);
		div$.removeClass("active");
	});
});