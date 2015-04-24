package edu.cmu.xbrw;

public class DownloadServlet extends javax.servlet.http.HttpServlet {
  public void doGet(javax.servlet.http.HttpServletRequest req,
                    javax.servlet.http.HttpServletResponse resp) throws java.io.IOException {
    this.doPost(req, resp);
  }
  public void doPost(javax.servlet.http.HttpServletRequest req,		     
                     javax.servlet.http.HttpServletResponse resp) throws java.io.IOException {

    resp.setHeader("Access-Control-Allow-Origin", "*");
    resp.setHeader("Access-Control-Allow-Methods",
		   "POST, GET, OPTIONS, DELETE");
    resp.setHeader("Access-Control-Max-Age", "3600");
    resp.setHeader("Access-Control-Allow-Headers", "x-requested-with");
    
    resp.setContentType("text/csv");

    resp.getWriter().write(req.getParameter("data"));    
  }
}
