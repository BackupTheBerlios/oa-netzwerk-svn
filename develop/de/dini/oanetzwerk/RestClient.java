/**
 * 
 */

package de.dini.oanetzwerk;


/**
 * @author Michael Kühn
 *
 */

public class RestClient {
	
	private boolean nossl;
	private String url;
	private String path;

	private RestClient (String url, String path) {
		
		this.url = filterurl (url);
		this.nossl = setSSL (url);
		this.path = filterpath (path);
	}
	
	/**
	 * @param path2
	 * @return
	 */
	private String filterpath (String path2) {

		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param url2
	 * @return
	 */
	private boolean setSSL (String url2) {

		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @param url2
	 * @return
	 */
	private String filterurl (String url2) {

		
		return null;
	}

	public static RestClient createRestClient (String incomming_url, String path) {
		
		RestClient restclient = new RestClient (incomming_url, path);
		
		return restclient;
	}
	
	/**
	 * @param args
	 */
	
	public static void main (String [ ] args) {

		// TODO Auto-generated method stub

	}

}
