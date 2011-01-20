

	var curmsg = -1;
	var messages = new Array();
	messages[0] = "Hundreds of great free backgrounds for the taking!";
	messages[1] = "Comprehensive DHTML scripts and components for your site!";
	messages[2] = "The JavaScript Technology center";
	//add more messages as desired

	var messagelinks = new Array();
	messagelinks[0] = "http://www.free-backgrounds.com";
	messagelinks[1] = "http://www.dynamicdrive.com";
	messagelinks[2] = "http://www.wsabstract.com";
	//add more links as indicated by the number of messages 

	function slidemessage() {
		if (curmsg < messages.length - 1) {
			curmsg++;
			
		} else {
			curmsg = 0;
		}
		document.slideshow[0].value = messages[curmsg];
		setTimeout("slidemessage()", 4500);
	}
	slidemessage();
