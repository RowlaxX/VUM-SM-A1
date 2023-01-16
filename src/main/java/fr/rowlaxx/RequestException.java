package fr.rowlaxx;

import java.net.http.HttpResponse;

import org.json.JSONObject;

/**
 * This class represent a request exception
 * Since binance encapsulate request exception in a JSON, we need to parse 
 * it to get the corresponding error message
 * 
 * @author Théo LINDER
 */
public class RequestException extends RuntimeException {
	private static final long serialVersionUID = 6382736077342981240L;

	/**
	 * Create a RequestException with a HttpResponse
	 * @param response the response
	 * @return the Exception
	 * @throws IllegalArgumentException if the status code is not an a error
	 */
	public static RequestException from(HttpResponse<String> response) {
		if (response.statusCode() < 200 && response.statusCode() >= 300)
			throw new IllegalArgumentException("The response is not an error");
		
		final JSONObject json = new JSONObject(response.body());
		
		//See https://binance-docs.github.io/apidocs/spot/en/#general-api-information
		return new RequestException(
				response.statusCode(), 
				json.getString("msg"), 
				json.getInt("code"));
	}
	
	private final int statusCode;
	private final int code;
	
	/**
	 * Default constructor
	 * @param statusCode
	 * @param message
	 * @param code
	 */
	private RequestException(int statusCode, String message, int code) {
		super(message);
		this.statusCode = statusCode;
		this.code = code;
	}
	
	/**
	 * Get the http status code
	 * @return
	 */
	public int getStatusCode() {
		return statusCode;
	}
	
	/**
	 * Get the error code originally present in the json
	 * @return the error code
	 */
	public int getCode() {
		return code;
	}
	
	@Override
	public String toString() {
		return "HTTP : " + statusCode + "    Code : " + code + "    Message : " + getMessage();
	}
}
