package com.jitsi.meetingpoc;

import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
public class AESHelper {

    // Method to generate a secret key
    private SecretKey generateKey() {
        byte[] keyBytes = new byte[] {0x2b, 0x7e, 0x15, 0x16, 0x28, 0x12, 0x42, 0x66, 0x09, 0x27, 0x4e, 0x4d, 0x20, 0x31, 0x6b, 0x7e};
        return new SecretKeySpec(keyBytes, "AES");
    }

    // Method to encrypt a plaintext string
    public String encrypt(String plainText) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, generateKey());
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    // Method to decrypt an encrypted string
    public String decrypt(String encryptedText) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, generateKey());
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedText);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes);
    }
}

