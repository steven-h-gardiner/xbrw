<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page" 
          xmlns:c="http://java.sun.com/jsp/jstl/core"
          version="1.2">
  <jsp:directive.page contentType="text/html;charset=UTF-8" language="java" />
  <jsp:scriptlet>
<![CDATA[
  com.google.appengine.api.users.UserService userService =
    com.google.appengine.api.users.UserServiceFactory.getUserService();

  String email = null;
  boolean loggedin = userService.isUserLoggedIn();
  if (userService.isUserLoggedIn()) {   
    email = userService.getCurrentUser().getEmail();
  }

  String appname = System.getProperty("app.name",
                                      com.google.apphosting.api.ApiProxy.getCurrentEnvironment().getAppId().replaceFirst("^s~", ""));
  // app.name can be specified as a system property in
  // appengine-web.xml, defaults to required appid.
  
  String loginURL = userService.createLoginURL(request.getRequestURI());
  String logoutURL = userService.createLogoutURL("/logout.jsp");
]]>
  </jsp:scriptlet>
  <c:set var="loggedin">
    <jsp:expression>loggedin</jsp:expression>
  </c:set>
  <c:set var="appname">
    <jsp:expression>appname</jsp:expression>
  </c:set>
  <c:set var="loginURL">
    <jsp:expression>loginURL</jsp:expression>
  </c:set>
  <c:set var="logoutURL">
    <jsp:expression>logoutURL</jsp:expression>
  </c:set>
  <c:set var="email">
    <jsp:expression>email</jsp:expression>
  </c:set>
  <jsp:text>
    <html>
      <head>
        <title>${appname} Entry Page</title>
        <style>
          body > div {
            width: 50%;
            max-width: 40em;
            min-width: 15em;
            margin-left: auto;
            margin-top: 5%;
            margin-right: auto;
            background-color: rgba(210,162,45,0.5);          
            border-radius: 1em;
            padding: 2% 10%;
          }

          #form_wrapper {
            text-align: center;
            margin: 5%;
            padding: 5%;
            border-radius: 5px;
            background-color: rgba(210,162,45,0.4);
          }
          
          #authentication-nag.loggedin_true, #authentication.loggedin_false {
            opacity: 0.5;
            display: none;
          }
          input#url { min-width: 70%; }
        </style>
      </head>
      <body>
        <div>
          <h1>Welcome to ${appname}</h1>
          <div id="authentication-nag" class="loggedin_${loggedin}">
            <h2>Authentication Required</h2>
            <p>Use of this service requires that you login using a
              Google account, and grant ${appname} permission to see
              your email address.</p>
            <p>You can login <a href="${loginURL}">here</a>.  You will
              first be asked to login to Google, then a second page
              will ask you to affirm access to your account by this
              site.  This site only checks emails to prevent
              abuse.</p>
          </div>
          <h2>Search Form</h2>
          <main id="form_wrapper">
            <form action="/mirror" method="get" accept-charset="utf-8">
              <div id="input_wrapper">
                <label for="url">URL: </label>
                <input type="text" name="url" value="" id="url"/><input id="go_button" type="submit" value="Go"/>
              </div>
            </form>            
          </main>
          <div style="display: none" id="warning">
            Fair use: All content belongs to the original copyright holders, respectively.
          </div>
          <footer id="authentication" class="loggedin_${loggedin}">          
 	    <h2>You are logged in as <span>${email}</span></h2>
            <p>You can logout <a href="${logoutURL}">here</a>.  Note: this will probably log you out of all Google services.</p>
          </footer>
        </div>
      </body>
    </html>
  </jsp:text>
</jsp:root>
