package web_updater.security;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.persistence.AttributeConverter;

public class EncryptionConverter implements AttributeConverter<String, String> {

	private Cipher cipher;

	public EncryptionConverter() {
		try {
			cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			e.printStackTrace();
		}
	}

	private void initCipher(int opmode) throws GeneralSecurityException {
		final String secret = System.getenv("DB_SECRET");
		cipher.init(opmode, new SecretKeySpec(
				Arrays.copyOf(MessageDigest.getInstance("SHA-1").digest(secret.getBytes()), 16),
				"AES"), new IvParameterSpec(new byte[16]));
	}

	// TODO test
	@Override
	public String convertToDatabaseColumn(String s) {
		try {
			initCipher(Cipher.ENCRYPT_MODE);
			return Base64.getEncoder().encodeToString(cipher.doFinal(s.getBytes()));
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
			return null;
		}
	}

	// TODO test
	@Override
	public String convertToEntityAttribute(String s) {
		try {
			initCipher(Cipher.DECRYPT_MODE);
			return new String(cipher.doFinal(Base64.getDecoder().decode(s)));
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void main(String[] args) {
		System.out.println("Hello World!");
	}
}
