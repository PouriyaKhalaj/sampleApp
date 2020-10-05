package ir.co.common.helperImpl

import android.util.Base64
import ir.co.common.helper.SecurityHelper
import java.nio.charset.Charset
import java.security.NoSuchAlgorithmException
import java.security.spec.InvalidKeySpecException
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec


class SecurityHelperImpl : SecurityHelper {

    private val pswdIterations = 10
    private val keySize = 128
    private val cypherInstance = "AES/CBC/PKCS5Padding"
    private val secretKeyInstance = "PBKDF2WithHmacSHA1"
    private val plainText = "sampleText"
    private val AESSalt = "exampleSalt"
    private val initializationVector = "8119745113154120"

    override fun encrypt(textToEncrypt: String): String {
        val skeySpec = SecretKeySpec(getRaw(plainText, AESSalt), "AES")
        val cipher = Cipher.getInstance(cypherInstance)
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, IvParameterSpec(initializationVector.toByteArray()))
        val encrypted = cipher.doFinal(textToEncrypt.toByteArray())
        return Base64.encodeToString(encrypted, Base64.DEFAULT)
    }

    override fun decrypt(textToDecrypt: String): String {

        val encrytedBytes = Base64.decode(textToDecrypt, Base64.DEFAULT)
        val skeySpec = SecretKeySpec(getRaw(plainText, AESSalt), "AES")
        val cipher = Cipher.getInstance(cypherInstance)
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, IvParameterSpec(initializationVector.toByteArray()))
        val decrypted = cipher.doFinal(encrytedBytes)
        return String(decrypted, Charset.forName("UTF-8"))
    }

    private fun getRaw(plainText: String, salt: String): ByteArray {
        try {
            val factory = SecretKeyFactory.getInstance(secretKeyInstance)
            val spec = PBEKeySpec(plainText.toCharArray(), salt.toByteArray(), pswdIterations, keySize)
            return factory.generateSecret(spec).encoded
        } catch (e: InvalidKeySpecException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }

        return ByteArray(0)
    }

}