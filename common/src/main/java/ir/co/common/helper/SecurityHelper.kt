package ir.co.common.helper

interface SecurityHelper {
    fun encrypt(textToEncrypt: String): String
    fun decrypt(textToDecrypt: String): String
}