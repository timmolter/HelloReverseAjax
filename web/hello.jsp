<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
    <head>
        <meta http-equiv="content-type" content="text/html;charset=utf-8" />
        <title>@TITLE@</title>
    </head>
    <body>
        <input type="button" onclick="reverseAjax()" value="Start Reverse Ajax!" />
        <h3 id="callbackmessage">&nbsp;</h3>
        
<script type="text/javascript">
        
	var xmlhttp = false; //same object for send and callback methods.
	
	function reverseAjax(){
		xmlhttp = new XMLHttpRequest();
		xmlhttp.open("POST", "async", true);
		xmlhttp.onreadystatechange = callback;
	    xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
	    xmlhttp.send();
	}
	
	function callback() {
		
	    if (xmlhttp.readyState == 4) {
	        if (xmlhttp.status == 200) {
	        	if (xmlhttp.responseText == "TIMEOUT") {
	    			// try again
	    			reverseAjax();
	    		} else {
	        		document.getElementById("callbackmessage").innerHTML = xmlhttp.responseText;
	        		reverseAjax();
	        	}
	        }else {
	            alert("An Error has occurred. Status = " + xmlhttp.status);
	         }
	    }
	}

</script>		

    </body>
</html>