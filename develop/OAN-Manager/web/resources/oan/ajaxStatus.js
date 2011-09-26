
	function processAjaxUpdate(msgId) {

		function processEvent(data) {
			 var msg = document.getElementById(msgId);
		    if (data.status === undefined) {
		      scrollTo(0,0);
		      msg.style.display = 'inline';
		    } else if (data.status == "success") {
		      msg.style.display = 'none';
		    }
		  }
		return processEvent;
	}
	
	function registerAjaxStatus(msgId) {
		jsf.ajax.addOnEvent(processAjaxUpdate(msgId));
	}