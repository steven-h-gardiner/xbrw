package edu.cmu.xbrw;

public class BlobServlet extends javax.servlet.http.HttpServlet {
  public void doGet(javax.servlet.http.HttpServletRequest req,
                    javax.servlet.http.HttpServletResponse resp) throws java.io.IOException {
    
    com.google.appengine.api.blobstore.BlobKey blobkey =
      new com.google.appengine.api.blobstore.BlobKey(req.getParameter("blobkey"));
    com.google.appengine.api.blobstore.BlobstoreService blobstoreService =
      com.google.appengine.api.blobstore.BlobstoreServiceFactory.getBlobstoreService();
    blobstoreService.serve(blobkey, resp);

  }
  public void doPost(javax.servlet.http.HttpServletRequest req,		     
                     javax.servlet.http.HttpServletResponse resp) throws java.io.IOException {
    String filename = "abc";
    com.google.appengine.api.files.FileService fileService =
      com.google.appengine.api.files.FileServiceFactory.getFileService();
    com.google.appengine.api.files.AppEngineFile file =
      fileService.createNewBlobFile("text/csv", filename);

    boolean lock = true;
    com.google.appengine.api.files.FileWriteChannel writeChannel =
      fileService.openWriteChannel(file, lock);
    
    java.io.PrintWriter pw =
      new java.io.PrintWriter(java.nio.channels.Channels.newOutputStream(writeChannel));

    pw.println(req.getParameter("data"));
    System.err.println("BLOBDATA: " + req.getParameter("data"));

    pw.close();
    System.err.println("PWCLOSED");
    writeChannel.closeFinally();
    System.err.println("WCCLOSED");

    org.json.JSONObject output = new org.json.JSONObject();
    output.putOpt("blobkey", fileService.getBlobKey(file).getKeyString());

    resp.setContentType("application/json");
    resp.getWriter().write(output.toString());
    resp.getWriter().close();

    BlobExpirer bexp = new BlobExpirer(fileService.getBlobKey(file).getKeyString());
    com.google.appengine.api.taskqueue.Queue queue = 
      com.google.appengine.api.taskqueue.QueueFactory.getDefaultQueue();
    queue.add(com.google.appengine.api.taskqueue.TaskOptions.Builder.withPayload(bexp).countdownMillis(1000*60*5));
    
  }

  public class BlobExpirer implements com.google.appengine.api.taskqueue.DeferredTask {
    public String blobstr = null;
    public BlobExpirer(String blobstr) {
      this.blobstr = blobstr;
    }
    public void run() {
      try {
	com.google.appengine.api.blobstore.BlobKey blobkey =
	  new com.google.appengine.api.blobstore.BlobKey(blobstr);
	com.google.appengine.api.blobstore.BlobstoreService blobstoreService =
	  com.google.appengine.api.blobstore.BlobstoreServiceFactory.getBlobstoreService();
	blobstoreService.delete(blobkey);
	
      } catch (Exception ex) {
	ex.printStackTrace(System.err);
      }
    }
  }
}
