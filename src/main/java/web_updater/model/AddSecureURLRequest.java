package web_updater.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddSecureURLRequest extends AddURLRequest {

	private String loginUrl;
	private String postData;

	public AddSecureURLRequest() {
		super();
	}

}
