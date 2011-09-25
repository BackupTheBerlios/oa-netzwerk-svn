
/**
 * This function toggles the visibility of the help text for a certain form element.
 * @param {Object} id
 */
function toggle_help(id){
   helpElement = document.getElementById(id);
   if (helpElement.style.display == 'block') {
	   helpElement.style.display = 'none';
   }
   else {
	   helpElement.style.display = 'block';
   }
   return false;
}

/**
 * These two lines are executed onLoad to hide all the help texts.
 */
if(document.getElementById && document.createElement) {
   document.write('<style type="text/css">*.help_box{display:none}</style>');
}


function hideElement(element) {
	
	document.getElementById(element).style.display = "none";
}
