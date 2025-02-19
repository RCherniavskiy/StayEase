package org.example.bookingapplication.telegram.util;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import org.example.bookingapplication.exception.telegram.EmailTokenGeneratorException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EmailTokenGenerator {
    private static final String ALGORITHM = "AES";
    private final SecretKeySpec secretKey;

    public EmailTokenGenerator(@Value("${telegram.secret}") String secret) {
        this.secretKey = new SecretKeySpec(secret.getBytes(), ALGORITHM);
    }

    public String encryptEmail(String email) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(email.getBytes());
            return Base64.getUrlEncoder().encodeToString(encryptedBytes);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException
                 | IllegalBlockSizeException | BadPaddingException e) {
            throw new EmailTokenGeneratorException("Cant encrypt email: " + e.getMessage());
        }
    }

    public String decryptEmail(String token) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedBytes = cipher.doFinal(Base64.getUrlDecoder().decode(token));
        return new String(decryptedBytes);
    }
}
