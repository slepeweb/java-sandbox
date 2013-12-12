/**
 ***********************************************************************
 ***********************************************************************
Copyright (C) 1996 - 2011 Alterian Technology Ltd. All rights reserved.

Alterian Technology Ltd
Alterian plc
The Spectrum Building
Bond Street
Bristol BS1 3LG, UK
+44 (0) 117 970 3200 +44 (0) 117 970 3201

http://www.alterian.com
info@alterian.com
 ***********************************************************************
File Information
================

%PID% %PRT% %PO%
%PM% - %PD%

 ***********************************************************************
 ***********************************************************************
 */

var PSLocale = Class.create({
	initialize: function(language) {
		this.language = language;
	},
	
	get: function(key) {
		var string = this.language[key];
		if (string == null) {
			string = key;
		}
		return string;
	}
});