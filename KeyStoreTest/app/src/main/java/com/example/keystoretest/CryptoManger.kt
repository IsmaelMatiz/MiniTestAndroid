package com.example.keystoretest


import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import androidx.annotation.RequiresApi
import java.io.InputStream
import java.io.OutputStream
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec


@RequiresApi(Build.VERSION_CODES.M)
class CryptoManger {

    private val keyStore = KeyStore.getInstance("AndroidKeyStore").apply {
        load(null)
    }

    private fun encryptCipher(chosenKey: KeyName): Cipher{
        return Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.ENCRYPT_MODE, getKey(chosenKey))
        }
    }

    private fun getDecryptCipherForIv(iv: ByteArray, chosenKey: KeyName): Cipher {
        return Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.DECRYPT_MODE, getKey(chosenKey), IvParameterSpec(iv))
        }
    }

    private fun getKey(chosenKey: KeyName): SecretKey{
        val existingKey = keyStore.getEntry(chosenKey.valKeyName, null) as? KeyStore.SecretKeyEntry
        return existingKey?.secretKey ?: createKey(chosenKey)
    }


    private fun createKey(chosenKey: KeyName): SecretKey{
        return  KeyGenerator.getInstance(ALGORITHM).apply {
            init(
                KeyGenParameterSpec.Builder(chosenKey.valKeyName,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(BLOCK_MODE)
                    .setEncryptionPaddings(PADDING)
                    .setUserAuthenticationRequired(false)
                    .setRandomizedEncryptionRequired(true)
                    .build()
            )
        }.generateKey()
    }

    fun encrypt(bytes: ByteArray, outputStream: OutputStream, chosenKey: KeyName): ByteArray? {
        try {
            val cipher = encryptCipher(chosenKey)
            val encryptedBytes = cipher.doFinal(bytes)
            outputStream.use {
                it.write(cipher.iv.size)
                it.write(cipher.iv)
                it.write(encryptedBytes.size)
                it.write(encryptedBytes)
            }
            return encryptedBytes
        }catch (e: Exception){
            Log.e("ErrorIsm","Error al Encriptar: $e")
        }
        return null
    }

    fun resetKeys(){
        keyStore.deleteEntry(KeyName.SECRET_KEY1.valKeyName)
        keyStore.deleteEntry(KeyName.SECRET_KEY2.valKeyName)
        keyStore.deleteEntry(KeyName.SECRET_KEY3.valKeyName)
        keyStore.deleteEntry(KeyName.SECRET_KEY4.valKeyName)
    }

    fun decrypt(inputStream: InputStream,chosenKey: KeyName): ByteArray? {
        try {
            return inputStream.use {
                val ivSize = it.read()
                val iv = ByteArray(ivSize)
                it.read(iv)

                val encryptedBytesSize = it.read()
                val encryptedBytes = ByteArray(encryptedBytesSize)
                it.read(encryptedBytes)

                getDecryptCipherForIv(iv,chosenKey).doFinal(encryptedBytes)
            }
        }catch (e: Exception){
            Log.e("ErrorIsm","Error al Desencriptar: $e")
        }
        return null
    }

    companion object{
        private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
        private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC
        private const val PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7
        private const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"
    }

}

enum class KeyName(val valKeyName: String) {
    SECRET_KEY1("secretKey1"),
    SECRET_KEY2("secretKey2"),
    SECRET_KEY3("secretKey3"),
    SECRET_KEY4("secretKey4")
}