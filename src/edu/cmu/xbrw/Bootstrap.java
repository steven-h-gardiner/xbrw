package edu.cmu.xbrw;

public class Bootstrap {
  public static void main(String[] args) throws Exception {
    //System.out.println("HELLO WORLD");

    java.io.OutputStream out = null;

    if (args.length > 0) {
      String arg0 = args[0];
      if (arg0.equals("-")) {
	out = System.out;
      } else {
	out = new java.io.FileOutputStream(new java.io.File(arg0));
      }
    }

    if (out == null) {
      java.io.File f = new java.io.File("build.xml");
      if (f.exists()) {
	System.err.println("REFUSING TO OVERWRITE build.xml; writing to STDOUT");
	out = System.out;
      } else {
	System.err.println("WRITING TO build.xml");
	out = new java.io.FileOutputStream(f);
      }
    }
 
    
    java.io.InputStream is = Bootstrap.class.getResourceAsStream("bootstrap.xml");
    //System.err.println("IS? " + (is != null));
    int count;
    byte[] buffer = new byte[8192];
    while ((count = is.read(buffer)) > 0) {
      out.write(buffer, 0, count);
    }
    
  }
}
