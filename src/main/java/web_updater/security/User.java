package web_updater.security;

import java.util.List;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "app_user")
@NoArgsConstructor
@Getter
@Setter
public class User {

	@Id
	private String email;
	private String name;
	@Convert(converter = EncryptionConverter.class)
	private String password;
	@ElementCollection
	private List<String> roles;

}
