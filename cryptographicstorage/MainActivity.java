package com.example.cryptographicstorage;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            String originalToken = "super_secret_api_session_token_123";

            // Encrypt
            String encryptedData = SecureStorageManager.encrypt(originalToken);
            Log.d("SECURITY_PROJECT", "Encrypted (Base64): " + encryptedData);

            // Decrypt
            String decryptedData = SecureStorageManager.decrypt(encryptedData);
            Log.d("SECURITY_PROJECT", "Decrypted: " + decryptedData);

        } catch (Exception e) {
            Log.e("SECURITY_PROJECT", "Crypto pipeline failed", e);
        }
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}