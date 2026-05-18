# Secure-Storage-Manager

This project is an automated solution designed for security consultants to cleanse and analyze client-provided documents. It processes various file formats, redacts sensitive information (PII, client names, logos), and uses an LLM to extract key security insights, ensuring all data is anonymized and ready for analysis.

<img width="1919" height="1019" alt="image" src="https://github.com/user-attachments/assets/3c729f2d-56e3-4594-8954-86c9afa8969c" />

### Key Security Features
 * **Hardware Isolation:** The 256-bit AES master key is generated directly inside the device's **Trusted Execution Environment (TEE)** or **StrongBox Secure Element (SE)**. The raw key material never enters the application process memory and cannot be exported.
 * **Authenticated Encryption (AEAD):** Uses `AES/GCM/NoPadding`. Galois/Counter Mode provides both confidentiality and data integrity. If an attacker modifies even a single bit of the encrypted string in storage, the cryptographic handshake fails during decryption rather than returning corrupted data.
 * **Dynamic Initialization Vectors (IV):** A unique, highly random 12-byte IV is automatically generated for every encryption operation, preventing replay and pattern-analysis attacks. The IV is safely prepended to the ciphertext payload for clean, single-string storage management.

### Prerequisites
 * Android Studio Jellyfish (or newer)
 * Minimum SDK: API 23 (Android 6.0 Marshmallow) or higher (required for native AES-GCM hardware support)
 * Language: Java

