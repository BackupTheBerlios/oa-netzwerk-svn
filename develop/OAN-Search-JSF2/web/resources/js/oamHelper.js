/**
 *  TODO: This code is normally generated in JSF 1.2 for Tomahawk DataScroller.
 *  	  In this case using JSF 2.0 this code is missing and have to imported.
 *        For this work around there should be found an solution.
 */
function oamSetHiddenInput(formname, name, value)
	{
		var form = document.forms[formname];
		if (typeof form == 'undefined')
		{
			form = document.getElementById(formname);
		}
		
		if(typeof form.elements[name]!='undefined' && (form.elements[name].nodeName=='INPUT' || form.elements[name].nodeName=='input'))
		{
			form.elements[name].value=value;
		}
		else
		{
			var newInput = document.createElement('input');
			newInput.setAttribute('type','hidden');
			newInput.setAttribute('id',name);
			newInput.setAttribute('name',name);
			newInput.setAttribute('value',value);
			form.appendChild(newInput);
		}
		
	}
	
	
	function oamClearHiddenInput(formname, name, value)
	{
		var form = document.forms[formname];
		if (typeof form == 'undefined')
		{
			form = document.getElementById(formname);
		}
		
		var hInput = form.elements[name];
		if(typeof hInput !='undefined')
		{
			form.removeChild(hInput);
		}
		
	}
	
	function oamSubmitForm(formName, linkId, target, params)
	{
		
		var clearFn = 'clearFormHiddenParams_'+formName.replace(/-/g, '\$:').replace(/:/g,'_');
		if(typeof window[clearFn] =='function')
		{
			window[clearFn](formName);
		}
		
		if(typeof window.getScrolling!='undefined')
		{
			oamSetHiddenInput(formName,'autoScroll',getScrolling());
		}
		
		var form = document.forms[formName];
		if (typeof form == 'undefined')
		{
			form = document.getElementById(formName);
		}
		
		var oldTarget = form.target;
		if(target != null)
		{
			
			form.target=target;
		}
		if((typeof params!='undefined') && params != null)
		{
			
			for(var i=0, param; (param = params[i]); i++)
			{
				oamSetHiddenInput(formName,param[0], param[1]);
			}
			
		}
		
		oamSetHiddenInput(formName,formName +':'+'_idcl',linkId);
		
		if(form.onsubmit)
		{
			var result=form.onsubmit();
			if((typeof result=='undefined')||result)
			{
				try
				{
					form.submit();
				}
				catch(e){}
			}
			
		}
		else 
		{
			try
			{
				form.submit();
			}
			catch(e){}
		}
		
		form.target=oldTarget;
		if((typeof params!='undefined') && params != null)
		{
			
			for(var i=0, param; (param = params[i]); i++)
			{
				oamClearHiddenInput(formName,param[0], param[1]);
			}
			
		}
		
		oamClearHiddenInput(formName,formName +':'+'_idcl',linkId);return false;
	}