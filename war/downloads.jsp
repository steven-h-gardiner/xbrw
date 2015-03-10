<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page" 
          xmlns:c="http://java.sun.com/jsp/jstl/core"
          version="1.2">
  <jsp:directive.page contentType="text/html;charset=UTF-8" language="java" />
  <jsp:scriptlet>
<![CDATA[

  System.err.println("FOO: " + application.getRealPath("/manifest.json"));
  
  java.io.FileReader r = new java.io.FileReader(new java.io.File(application.getRealPath("/manifest.json")));
  org.json.JSONObject man = new org.json.JSONObject(new org.json.JSONTokener(r));

  System.err.println("MAN: " + man.toString());

  String basename = man.optString("name", "xbrw");
]]>
  </jsp:scriptlet>
  <c:set var="basename">
    <jsp:expression>basename</jsp:expression>
  </c:set>
  <jsp:text>
    <html>
      <head>
        <title>${appname} Downloads</title>
      </head>
      <body>
	<h2>Downloads</h2>
	<h3>Firefox</h3>
	<div>
	  To install the Firefox extension, click <a href="/${basename}.xpi">here</a>
	</div>
	<h3>Chrome</h3>
	<div>
	  To install the Chrome extension
	  <ol>
	    <li>Download and save the extension to your desktop, from <a href="/${basename}.crx">here</a></li>
	    <li>Open the Extensions page by clicking the Chrome menu, then selecting More Tools -&gt; Extensions</li>
	    <li>Drag-and-drop the saved file onto the Extensions page</li>
	  </ol>
	</div>
	<h3>Internet Explorer, Safari, Others</h3>
	<div>
	  To use the service through a proxy, copy the URL and then go <a href="/index.jsp">here</a>
	</div>
	<h2>Advanced Options</h2>
	<h3>Greasemonkey (Firefox)</h3>
	<div>
	  To install a Greasemonkey script, click <a href="/${basename}.user.js">here</a>
	</div>
	<h3>Tampermonkey (Chrome)</h3>
	<div>
	  To install a Tampermonkey script, click <a href="/${basename}.tamper.js">here</a>
	</div>
      </body>
    </html>
  </jsp:text>
</jsp:root>
