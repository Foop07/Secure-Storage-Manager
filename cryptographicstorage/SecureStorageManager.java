package com.example.cryptographicstorage;

import android.security.keystore.KeyProperties;
import android.security.keystore.KeyGenParameterSpec;
import android.util.Base64;
import java.security.KeyStore;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

public class SecureStorageManager {

    private static final String ANDROID_KEYSTORE = "AndroidKeyStore";
    private static final String KEY_ALIAS = "AppSecretKeyAlias";
    private static final String AES_MODE = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12; // 12 bytes recommended for GCM

    // 1. Generate or Retrieve the Hardware-Backed Master Key
    private static SecretKey getOrCreateSecretKey() throws Exception {
        KeyStore keyStore = KeyStore.getInstance(ANDROID_KEYSTORE);
        keyStore.load(null);

        // If key already exists, just return it
        if (keyStore.containsAlias(KEY_ALIAS)) {
            return ((KeyStore.SecretKeyEntry) keyStore.getEntry(KEY_ALIAS, null)).getSecretKey();
        }

        // Otherwise, generate a new hardware-isolated key
        KeyGenerator keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE);

        KeyGenParameterSpec keyGenParameterSpec = new KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build();

        keyGenerator.init(keyGenParameterSpec);
        return keyGenerator.generateKey();
    }

    // 2. Encrypt Plaintext (Outputs IV + Ciphertext joined together)
    public static String encrypt(String plainText) throws Exception {
        SecretKey secretKey = getOrCreateSecretKey();
        Cipher cipher = Cipher.getInstance(AES_MODE);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        byte[] iv = cipher.getIV();
        byte[] cipherText = cipher.doFinal(plainText.getBytes("UTF-8"));

        // Prepend IV to the ciphertext so it can be retrieved during decryption
        byte[] combined = new byte[iv.length + cipherText.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(cipherText, 0, combined, iv.length, cipherText.length);

        return Base64.encodeToString(combined, Base64.DEFAULT);
    }

    // 3. Decrypt Ciphertext
    public static String decrypt(String encryptedBase64) throws Exception {
        byte[] combined = Base64.decode(encryptedBase64, Base64.DEFAULT);

        // Extract IV
        byte[] iv = new byte[GCM_IV_LENGTH];
        System.arraycopy(combined, 0, iv, 0, iv.length);

        // Extract Ciphertext
        int cipherTextLength = combined.length - GCM_IV_LENGTH;
        byte[] cipherText = new byte[cipherTextLength];
        System.arraycopy(combined, GCM_IV_LENGTH, cipherText, 0, cipherTextLength);

        SecretKey secretKey = getOrCreateSecretKey();
        Cipher cipher = Cipher.getInstance(AES_MODE);
        GCMParameterSpec spec = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);

        byte[] plainTextBytes = cipher.doFinal(cipherText);
        return new String(plainTextBytes, "UTF-8");
    }
}