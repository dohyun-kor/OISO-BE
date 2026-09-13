package com.nigahae.tourcourse.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class EncryptedTourCoursePasswordEncoder implements PasswordEncoder {
    private final SecretKeySpec key;
    private final SecureRandom random = new SecureRandom();

    public EncryptedTourCoursePasswordEncoder(
            @Value("${tour-course.password-encryption-key:}") String configuredKey) {
        if (configuredKey == null || configuredKey.length() < 16) {
            throw new IllegalStateException("TOUR_COURSE_PASSWORD_ENCRYPTION_KEY must be at least 16 characters");
        }
        this.key = new SecretKeySpec(configuredKey.getBytes(StandardCharsets.UTF_8), 0, 16, "AES");
    }

    @Override
    public String encode(CharSequence rawPassword) {
        try {
            byte[] iv = new byte[12]; random.nextBytes(iv);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(128, iv));
            byte[] encrypted = cipher.doFinal(rawPassword.toString().getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(iv) + "." + Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) { throw new IllegalStateException("Course password encryption failed", e); }
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        try { return decrypt(encodedPassword).equals(rawPassword.toString()); }
        catch (RuntimeException e) { return false; }
    }

    public String decrypt(String encodedPassword) {
        try {
            String[] parts = encodedPassword.split("\\.", 2);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, key,
                    new GCMParameterSpec(128, Base64.getDecoder().decode(parts[0])));
            return new String(cipher.doFinal(Base64.getDecoder().decode(parts[1])), StandardCharsets.UTF_8);
        } catch (Exception e) { throw new IllegalStateException("Course password decryption failed", e); }
    }
}
