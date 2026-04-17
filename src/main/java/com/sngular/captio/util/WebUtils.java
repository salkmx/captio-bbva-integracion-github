/**
 * 
 */
package com.sngular.captio.util;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

/**
 * Esta clase contiene los elementos Util para los elementos http /peticiones | requests
 * @author Rafael Miranda - rmiranda
 * @since 12/2024
 */
public class WebUtils {
	private HttpHeaders headers = null;
	private String customKey;
	
	public WebUtils(String customKey) {
		headers = new HttpHeaders();
		this.customKey = customKey; 
	}
	
	public HttpHeaders getHeaders(){
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("customerKey", customKey);
		return headers;
	}
}
