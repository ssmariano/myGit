import java.io.Serializable;

/***************************************************************************
*   Seguranca e Confiabilidade 2016/17
*
*
***************************************************************************/


/**
 * Object used to send boolean result and status message to the client.
 * @author fc30396
 *
 */
public class ResultMessage implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Boolean success = false;
	private String message = null;
	
	public ResultMessage(Boolean success, String message) {
		this.success = success;
		this.message = message;
	}
	
	public ResultMessage() {
		
	}

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
}