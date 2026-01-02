package com.example.blog.controller;

import io.imagekit.sdk.ImageKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.blog.dto.ImageKitAuthResponse;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.UUID;

/**
 * Controller for ImageKit authentication.
 * Provides authentication parameters for client-side uploads.
 */
@RestController
@RequestMapping("/api/imagekit")
public class ImageKitAuthController {

    private static final Logger log = LoggerFactory.getLogger(ImageKitAuthController.class);

    private final ImageKit imageKit;

    @Value("${imagekit.private-key}")
    private String privateKey;

    public ImageKitAuthController(ImageKit imageKit) {
        this.imageKit = imageKit;
    }

    /**
     * Returns authentication parameters for client-side ImageKit uploads.
     * The frontend uses these to directly upload images to ImageKit.
     *
     * @return ImageKitAuthResponse containing token, expire (seconds), and signature
     */
    @GetMapping("/auth")
    public ResponseEntity<ImageKitAuthResponse> getAuthenticationParameters() {
        // Always generate manually to ensure consistent expire handling
        try {
            ImageKitAuthResponse authResp = generateAuthResponse();
            log.debug("Generated auth params manually: {}", authResp);
            return ResponseEntity.ok(authResp);
        } catch (Exception e) {
            log.error("Failed to generate authentication parameters", e);
            throw new RuntimeException("Failed to generate ImageKit authentication parameters", e);
        }
    }

    private long parseExpire(String expireRaw) {
        long now = System.currentTimeMillis() / 1000;
        if (expireRaw == null || expireRaw.isEmpty()) {
            return now + 1800; // default 30 minutes
        }

        try {
            long parsed = Long.parseLong(expireRaw);

            // If expire looks like milliseconds (timestamp > 9999999999), convert to seconds
            if (parsed > 9_999_999_999L) {
                parsed = parsed / 1000;
            }

            /*
             * The SDK (or some providers) may return either:
             * - an absolute unix timestamp (seconds since epoch), or
             * - a relative number of seconds (duration) until expiry.
             *
             * If the parsed value is in the past (less than 'now'), treat it as a relative
             * duration and add it to 'now'. This handles cases where the SDK returns "600"
             * meaning "600 seconds from now".
             */
            if (parsed < now) {
                parsed = now + parsed;
            }

            // Ensure expire is not more than 1 hour from now (ImageKit requires < 1 hour)
            if (parsed > now + 3600) {
                parsed = now + 3599; // cap just under 1 hour
            }

            return parsed;
        } catch (Exception ex) {
            return now + 1800; // fallback 30 minutes
        }
    }

    private ImageKitAuthResponse generateAuthResponse() throws NoSuchAlgorithmException, InvalidKeyException {
        String token = UUID.randomUUID().toString();
        long expire = (System.currentTimeMillis() / 1000) + 1800; // 30 minutes
        String signatureData = token + expire;
        String signature = hmacSha1(signatureData, privateKey);
        log.info("Generated expire: {} ({} seconds from now)", expire, expire - (System.currentTimeMillis() / 1000));
        return new ImageKitAuthResponse(token, expire, signature);
    }

    /**
     * Generates HMAC-SHA1 signature.
     */
    private String hmacSha1(String data, String key) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance("HmacSHA1");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA1");
        mac.init(secretKeySpec);
        byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(hash);
    }

    /**
     * Converts byte array to hexadecimal string.
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
