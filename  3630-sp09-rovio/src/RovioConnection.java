import java.net.*;
import java.io.*;

/**
 * Provides basic interactions with the Rovio's web server.
 * @author Sean Hussey
 */
public class RovioConnection {
	private String host;
	
	/**
	 * Defines a connection the the Rovio robot.
	 * @param host	the address of the Rovio's web server.
	 */
	public RovioConnection(final String host) {
		setHost(host);
	}
	
	/**
	 * Defines a password-protected connection to the Rovio robot.
	 * @param host	the address of the Rovio's web server.
	 * @param userName	the name of the user.
	 * @param password	the password for the given user.
	 */
	public RovioConnection(final String host, final String userName, final String password) {
		this(host);

		Authenticator.setDefault(new Authenticator() {
			final PasswordAuthentication auth = new PasswordAuthentication(userName, password.toCharArray());
			
			protected PasswordAuthentication getPasswordAuthentication() {
				return auth;  
			}
		});		
	}
	
	public void setHost(final String host) {
		this.host = host;
	}
		
	public String getHost() {
		return host;
	}
	
	/**
	 * Opens the given URL for reading.
	 * @param url	the page's URL.
	 * @return		the server's response.
	 * @throws IOException if the connection fails.
	 */
	public static InputStream open(URL url) throws IOException {
		URLConnection connection = url.openConnection();
				
		connection.connect();
		
		return connection.getInputStream();
	}
	
	/**
	 * Opens the given resource on the Rovio for reading.
	 * <br/>Equivalent to calling <code>open(new URL("http",getHost(),resourceName))</code>.
	 * @param resource	the resource to read from. 
	 * @return
	 */
	public InputStream open(String resource) throws IOException {
		// Ensure that the file is prefixed with a backslash
		if(!resource.startsWith("//"))
			resource = "//" + resource;
		
		try {
			return open(new URL("http", host, resource));
		} catch(MalformedURLException e) {
			throw new IOException(e);
		}
	}
	
	public InputStream open(String resource, Object... args) throws IOException {
		// Ensure early failure if odd number of argument name/value pairings
		if(args.length % 2 != 0)
			throw new IOException(new IllegalArgumentException("Every argument name must have an associated value"));
		
		// Append the arguments to the end of the resource
		for(int i = 0; i < args.length; i += 2) {
			resource += (i == 0 ? "?" : "&") + args[i] + "=" + args[i + 1];
		}
		
		return open(resource);
	}
	
	// Debugging/test main method.
	public static void main(String[] args) {
		// Create a password-protected connection to the robot
		RovioConnection rovio; 
//		rovio = new RovioConnection("http://3630rovio.servebeer.com", "admin", "3630class");
		rovio = new RovioConnection("192.168.10.18");
		
		try {
			// Query the robot's status
			InputStream in;
//			URL url = new URL(rovio.getHost() + "/rev.cgi?Cmd=nav&action=1");
//			in = rovio.open(url);
			in = rovio.open("rev.cgi", "Cmd", "nav", "action", 1);
			
			// Display the result
			// Note: Use a Scanner to process this.
			int c = in.read();
			while(c >= 0) {
				System.out.print((char)c);
				c = in.read();
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
