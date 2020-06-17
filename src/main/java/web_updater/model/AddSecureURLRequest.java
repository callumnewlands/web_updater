package web_updater.model;

public class AddSecureURLRequest extends AddURLRequest {

	private String loginUrl;
	private String postData;

	public AddSecureURLRequest() {
		super();
	}

	public String getLoginUrl() {
		return loginUrl;
	}

	public void setLoginUrl(String loginUrl) {
		this.loginUrl = loginUrl;
	}

	public String getPostData() {
		return postData;
	}

	public void setPostData(String postData) {
		this.postData = postData;
	}
}
