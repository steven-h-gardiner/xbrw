package edu.cmu.mixer.proxy;

public class MirrorServlet  extends javax.servlet.http.HttpServlet {
  public static java.util.HashMap<java.util.regex.Pattern, String> replacers = new java.util.HashMap<java.util.regex.Pattern, String>();
  static {
    replacers.put(java.util.regex.Pattern.compile("(?<prefix>href=[\"\'])(?<refract>/.+?)(?<suffix>[\"\'])", java.util.regex.Pattern.CASE_INSENSITIVE), "${pre}${url}${post}");
    replacers.put(java.util.regex.Pattern.compile("(?<prefix>action=[\"\'])(?<refract>/.+?)(?<suffix>[\"\'])", java.util.regex.Pattern.CASE_INSENSITIVE), "${pre}${url}${post}");
    //replacers.put(java.util.regex.Pattern.compile("(?<prefix>href=[\"\'])(?<refract>.+?)(?<suffix>[\"\'])", java.util.regex.Pattern.CASE_INSENSITIVE), "${pre}${url}${post}");
    //replacers.put(java.util.regex.Pattern.compile("(?<prefix>href=)(?<refract>[^\\s\"\'=\\<\\>]+)(?<suffix>.)", java.util.regex.Pattern.CASE_INSENSITIVE), "${pre}${url}${post}");
    //replacers.put(java.util.regex.Pattern.compile("(?<prefix>src=[\"\'])(?<resolve>.*?)(?<suffix>[\"\'])", java.util.regex.Pattern.CASE_INSENSITIVE), "${pre}${url}${post}");
    //replacers.put(java.util.regex.Pattern.compile("(?<prefix>src=)(?<resolve>[^\\s\"\'=\\<\\>]+)(?<suffix>.)", java.util.regex.Pattern.CASE_INSENSITIVE), "${pre}${url}${post}");
    //replacers.put(java.util.regex.Pattern.compile("(?<prefix>background=[\"\'])(?<resolve>.*?)(?<suffix>[\"\'])", java.util.regex.Pattern.CASE_INSENSITIVE), "${pre}${url}${post}");
  }
  public static java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
  public static String getTimestamp() {
    return sdf.format(new java.util.Date());
  }

  public static boolean isNonProxied(java.net.URL url) throws Exception {
    java.net.URL url2 = new java.net.URL(url.getProtocol(),
					 url.getHost(),
					 url.getPort(),
					 url.getPath());
				       
    return isNonProxied(url2.toString());
  }

  public static boolean isNonProxied(String url) {
    boolean nonproxied = false;
    nonproxied = nonproxied || url.endsWith(".gif");
    nonproxied = nonproxied || isNonProxied(url, System.getProperty("com.google.appengine.application.id", "localhost"));
    nonproxied = nonproxied || isNonProxied(url, "localhost");
    return nonproxied;
  }
  
  public static boolean isNonProxied(String url, String substring) {
    int ix = url.toString().indexOf(substring);
    if (ix >= 0) {
      if (ix <= 20) {
        return true;
      }
    }
     
    return false;
  }
  
  public static com.google.appengine.api.datastore.DatastoreService ds =
    com.google.appengine.api.datastore.DatastoreServiceFactory.getDatastoreService();  
  
  public void doGet(javax.servlet.http.HttpServletRequest req,
                    javax.servlet.http.HttpServletResponse resp) throws java.io.IOException {
    this.doPost(req, resp);
  }

  public String refract(String absolute, java.net.URL mirrorURL) throws Exception {
    String refracted = mirrorURL.toString().replaceFirst("(https?://.+?/.+?/).*", "$1") + absolute;
    return refracted;
  }
  
  public String resolve(String dangle, java.net.URL resolveURL) throws Exception {
    String resolved = new java.net.URL(resolveURL, dangle).toString();
    return resolved;
  }
  
  public void rewrite(java.io.InputStream in, java.io.OutputStream out, java.net.URL mirrorURL, java.net.URL resolveURL) throws Exception {
    rewrite(new java.io.InputStreamReader(in), new java.io.OutputStreamWriter(out), mirrorURL, resolveURL);
  }

  public void rewrite(java.io.Reader in, java.io.Writer out, java.net.URL mirrorURL, java.net.URL resolveURL) throws Exception {
    java.io.BufferedReader br = new java.io.BufferedReader(in);

    boolean delivered = false;

    String inLine = null;    
    while ((inLine = br.readLine()) != null) {
      String outLine = inLine;

      for (java.util.Map.Entry<java.util.regex.Pattern,String> entry : replacers.entrySet()) {
        try {
          java.util.regex.Pattern p = entry.getKey();
          String s = entry.getValue();
          String t = "";
          
          java.util.regex.Matcher m = p.matcher(outLine);
          //System.err.println("MATCH: " + outLine);
          
          int ix = 0;
          while (m.find(ix)) {          
            t += outLine.substring(ix, m.start());            

	    String dangle = null;
	    String fixed = null;
	    try {
	      if (m.group("refract") != null) {	      
		fixed = refract(resolve(m.group("refract"), resolveURL), mirrorURL);
		dangle = m.group("refract");
	      }
	    } catch (IllegalArgumentException iae) { }
	    try {
	      if (m.group("resolve") != null) {
		fixed = resolve(m.group("resolve"), resolveURL);
		dangle = m.group("resolve");
	      }
	    } catch (IllegalArgumentException iae) { }
	    	    
	    if (fixed != null) {
	      //System.err.printf("FIX: |%s| -> |%s| (%s) \n", dangle, fixed, outLine);

	      t += m.group("prefix");
	      t += fixed;
	      t += m.group("suffix");            
            }
	    
            ix = m.end();

            //System.err.println("REMATCH: " + outLine.substring(ix));
          }

          t += outLine.substring(ix);
          
          outLine = t;
        } catch (Exception ex) {
          ex.printStackTrace(System.err);
        }
      }
      
      //System.err.printf("REWRITE |%s| -> |%s|\n", inLine, outLine);
            
      out.write(outLine);
      out.write("\n");
    }

    if (!delivered) {
      String mirrorBase = mirrorURL.toString();
      mirrorBase += (resolveURL.getPath().equals("")) ? "/" : "";

      String baseElt = "<base class='mirror' data-resolve='"+resolveURL.toString()+"' href='" + mirrorBase + "'></base>";
      //System.err.printf("MANIFEST: %s\n", getManifest()); 
                         
      out.write(baseElt);

      
      
      if (false) {
        out.write("<script>var module={}; module.exports={}; module.jQuery = window.jQuery;</script>");
        out.write("<script>module.resolveurl = '" + resolveURL + "';</script>");
        out.write("<script>module.refracturl = '" + mirrorURL + "';</script>");
        out.write("<script src='/jquery-2.1.1.js'></script>");
        out.write("<script>var ijQuery = module.exports;</script>");
        out.write("<script>console.error('IJ');</script>");
        out.write("<script>var jQuery = ijQuery;</script>");
        out.write("<script src='/hello_world.js'></script>");
        //out.write("<style src='/helloworld.css'></style>");
        out.write("<link rel='stylesheet' href='/helloworld.css'></link>");      
        out.write("<script>jQuery = module.jQuery;</script>");
      } else {
        //System.err.printf("PAYLOAD |%s| -> |%s|\n", getPayload(), java.text.MessageFormat.format(getPayload(), resolveURL.toString(), mirrorURL.toString()));
        out.write(java.text.MessageFormat.format(getPayload(), resolveURL.toString(), mirrorURL.toString()));
      }
    }
    
    out.close();
  }

  public static com.google.appengine.api.users.UserService userService =
    com.google.appengine.api.users.UserServiceFactory.getUserService();

  public org.json.JSONObject manifest = null;
  public org.json.JSONObject getManifest() throws Exception {
    if (manifest == null) {
      java.io.InputStream resourceContent = getServletContext().getResourceAsStream("/manifest.json");
      java.util.Scanner scan = new java.util.Scanner(resourceContent);

      StringBuffer sb = new StringBuffer();
      while (scan.hasNextLine()) {
        sb.append(scan.nextLine());
      }
      //System.err.println("MANIFEST: " + sb.toString());
      manifest = new org.json.JSONObject(sb.toString());
    }
    return manifest;
  }

  public String payload = null;
  public String getPayload() throws Exception {
    if (payload == null) {
      org.json.JSONObject manifest = getManifest();
      org.json.JSONArray scripts = manifest.optJSONArray("content_scripts");

      org.json.JSONObject sources = manifest.optJSONObject("sources");
      if (sources == null) {
        sources = new org.json.JSONObject();
      }
      
      StringBuffer sb = new StringBuffer();

      if (scripts != null) {
        for (int i = 0; i < scripts.length(); i++) {
          org.json.JSONObject script = scripts.optJSONObject(i);

          String runat = script.optString("run_at", "document_idle");
          if (runat.equals("disabled")) { continue; }
          
          org.json.JSONArray js = script.optJSONArray("js");
          if (js != null) {
            for (int j = 0; j < js.length(); j++) {
              String jspath = js.optString(j);
              sb.append("<script data-resolve=''{0}'' data-refract=''{1}'' src='/");
              sb.append(jspath);
              sb.append("'></script>");
              sb.append("\n");
            }
          }

          org.json.JSONArray css = script.optJSONArray("css");
          if (js != null) {
            for (int j = 0; j < css.length(); j++) {
              String csspath = css.optString(j);
              String csssrc = sources.optString(csspath, "/" + csspath);
              if (csssrc.startsWith("file:")) {
                csssrc = "/" + csspath;
              }
              sb.append("<link data-resolve=''{0}'' data-refract=''{1}'' rel='stylesheet' href='");
              sb.append(csssrc);
              sb.append("'></link>");
              sb.append("\n");
            }
          }

          org.json.JSONArray html = script.optJSONArray("html");
          if (html != null) {
            for (int j = 0; j < html.length(); j++) {
              String htmlpath = html.optString(j);
              sb.append("<div style=''display:none'' data-resolve=''{0}'' data-refract=''{1}'' data-src=''/");
              sb.append(htmlpath);
              sb.append("''>");
              java.util.Scanner scan = new java.util.Scanner(getServletContext().getResourceAsStream("/" + htmlpath));
              while (scan.hasNextLine()) {
                sb.append(scan.nextLine());
              }
             
              sb.append("</div>");
              sb.append("\n");
            }
          }
        }
      }
      
      payload = sb.toString();
      System.err.println("PAYLOAD: " + payload);
    }
    return payload;
  }
  
  public void doPost(javax.servlet.http.HttpServletRequest req,
                     javax.servlet.http.HttpServletResponse resp) throws java.io.IOException {

    org.json.JSONObject json = new org.json.JSONObject();
    try {
      
      json.putOpt("now", new java.util.Date().toString());
      json.putOpt("requestURL", req.getRequestURL().toString());
      json.putOpt("requestURI", req.getRequestURI().toString());
      json.putOpt("serverName", req.getServerName().toString());
      json.putOpt("context", getServletContext().getContextPath().toString());
      json.putOpt("serverInfo", getServletContext().getServerInfo().toString());
      json.putOpt("realPath", getServletContext().getRealPath(json.optString("requestURI", "/")).toString());
      json.putOpt("appid", System.getProperty("com.google.appengine.application.id"));
      json.putOpt("qstr", req.getQueryString());
      json.putOpt("who", userService.getCurrentUser().getEmail());
      json.putOpt("method", req.getMethod());
      
      String suffix = req.getRequestURI().toString().replaceFirst("\\/.*?\\/", "");
      json.putOpt("suffix", suffix);

      try {
        java.net.URL url = null;
        if (suffix.equals(req.getRequestURI().toString())) {
          if (req.getParameter("url") != null) {
            resp.sendRedirect(req.getRequestURL().toString() + "/" + req.getParameter("url"));
            return;
          }
        }

        if (url == null) {
           try {
            if (url == null) {
              url = new java.net.URL(suffix + "?" + req.getQueryString());
            }
          } catch (Exception ex2) {
          }

          try {
            if (url == null) {
              url = new java.net.URL("http://" + suffix + "?" + req.getQueryString());
            }
          } catch (Exception ex2) {
          }
         
        }

        if (url.getPath().equals("")) {
          resp.sendRedirect(req.getRequestURL().toString() + "/");
          return;
        }
        
	System.err.println("PROXYING: " + url.toString());
        if (MirrorServlet.isNonProxied(url)) {
          System.err.println("NP REDIRECT: " + url.toString());
          resp.sendRedirect(url.toString());
          return;
        }

        if (true) {
          java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
          conn.setConnectTimeout(0); // infinite timeout
          System.err.println("2PROXYING: " + conn.getURL().toString());
          //conn.setDoOutput(true);
          conn.setRequestMethod(req.getMethod());
          if (req.getMethod().equals("POST")) {
            conn.setDoOutput(true);
          }
          for (java.util.Enumeration e = req.getHeaderNames() ; e.hasMoreElements() ;) {
            String name = e.nextElement().toString();
            if (! name.equals("Host")) { 
	      conn.setRequestProperty(name, req.getHeader(name));
	    }
          }
          System.err.println("PROPS: " + conn.getRequestProperties());

          if (req.getMethod().equals("POST")) {
	    System.err.println("POST");
            java.io.InputStream in1 = req.getInputStream();
            java.io.OutputStream out1 = conn.getOutputStream();
            int count1;
            byte[] buffer1 = new byte[8192];
            while ((count1 = in1.read(buffer1)) > 0) {
              out1.write(buffer1, 0, count1);
	      //System.err.println(new String(buffer1, 0, count1));
            }
            System.err.println();
	    System.err.println("/POST");
          }
          String contentType = conn.getContentType();
          
          //System.err.println("CTYPE: " + contentType);
          resp.setContentType(contentType);          

          System.err.println("JSON: " + json.toString(2));

          if (contentType.startsWith("text/html")) {
	    if (! Boolean.parseBoolean(System.getProperty("dont.log.rewrites", "false"))) {
	      com.google.appengine.api.datastore.Entity event =
		new com.google.appengine.api.datastore.Entity("Rewrite");

	      event.setProperty("timestamp", getTimestamp());
	      event.setProperty("userAgent", req.getHeader("user-agent"));

	      ds.put(event);
	    }
	    
            rewrite(conn.getInputStream(), resp.getOutputStream(), new java.net.URL(req.getRequestURL().toString()), url);
              
            return;  
          }
          
          java.io.InputStream in = conn.getInputStream();
          int count;
          byte[] buffer = new byte[8192];
          while ((count = in.read(buffer)) > 0) {
            resp.getOutputStream().write(buffer, 0, count);
          }
       
          return;
        }  
      } catch (Exception ex1) {
        ex1.printStackTrace(System.err);
      }
      
      resp.setContentType("application/json");
      resp.getWriter().write(json.toString(2));
      resp.getWriter().write("\n");
    } catch (Exception ex) {
      ex.printStackTrace(System.err);
      resp.setContentType("text/plain");
      ex.printStackTrace(resp.getWriter());
    }
    
  }

}
