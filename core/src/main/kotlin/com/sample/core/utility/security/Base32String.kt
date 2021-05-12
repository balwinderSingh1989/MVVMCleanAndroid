package com.sample.core.utility.security

import java.util.*
import kotlin.jvm.Throws

/**
 * Encodes arbitrary byte arrays as case-insensitive base-32 strings.
 *
 *
 * The implementation is slightly different than in RFC 4648. During encoding,
 * padding is not added, and during decoding the last incomplete chunk is not
 * taken into account. The result is that multiple strings decode to the same
 * byte array, for example, string of sixteen 7s ("7...7") and seventeen 7s both
 * decode to the same byte array.
 * 
 * Ref : https://github.com/heapsource/google-authenticator.android/blob/master/src/com/google/android/apps/authenticator/Base32String.java
 */
class Base32String
/**
 * Instantiates a new Base 32 string.
 *
 * @param alphabet the alphabet
 */
protected constructor(// 32 alpha-numeric characters.
    private val alphabet: String
) {
    private val digits: CharArray = this.alphabet.toCharArray()
    private val mask: Int
    private val shift: Int
    private val charMap: HashMap<Char, Int>

    init {
        mask = digits.size - 1
        shift = Integer.numberOfTrailingZeros(digits.size)
        charMap = HashMap()
        for (i in digits.indices) {
            charMap[digits[i]] = i
        }
    }

    /**
     * Decode internal byte [ ].
     *
     * @param encoded the encoded
     * @return the byte [ ]
     * @throws DecodingException the decoding exception
     */
    @Throws(DecodingException::class)
    protected fun decodeInternal(encoded: String): ByteArray {

        var tempEncode: String =
            encoded.trim { it <= ' ' }.replace(SEPARATOR.toRegex(), "")
                .replace(" ".toRegex(), "")
        // Remove whitespace and separators

        // Remove padding. Note: the padding is used as hint to determine how many
        // bits to decode from the last incomplete chunk (which is commented out
        // below, so this may have been wrong to start with).
        tempEncode = tempEncode.replaceFirst("[=]*$".toRegex(), "")

        // Canonicalize to all upper case
        tempEncode = tempEncode.toUpperCase(Locale.US)
        if (tempEncode.isEmpty()) {
            return ByteArray(0)
        }
        val encodedLength = tempEncode.length
        val outLength = encodedLength * shift / 8
        val result = ByteArray(outLength)
        var buffer = 0
        var next = 0
        var bitsLeft = 0
        for (c in tempEncode.toCharArray()) {
            if (!charMap.containsKey(c)) {
                throw DecodingException("Illegal character: $c")
            }
            buffer = buffer shl shift
            buffer = buffer or (charMap[c] as Int and mask)
            bitsLeft += shift
            if (bitsLeft >= 8) {
                result[next++] = (buffer shr bitsLeft - 8).toByte()
                bitsLeft -= 8
            }
        }
        // We'll ignore leftover bits for now.
        //

        return result
    }

    /**
     * Encode internal string.
     *
     * @param data the data
     * @return the string
     */
    protected fun encodeInternal(data: ByteArray): String {
        if (data.isEmpty()) {
            return ""
        }

        // shift is the number of bits per output character, so the length of the
        // output is the length of the input multiplied by 8/shift, rounded up.
        if (data.size >= 1 shl 28) {
            // The computation below will fail, so don't do it.
            throw IllegalArgumentException()
        }

        val outputLength = (data.size * 8 + shift - 1) / shift
        val result = StringBuilder(outputLength)

        var buffer = data[0].toInt()
        var next = 1
        var bitsLeft = 8
        while (bitsLeft > 0 || next < data.size) {
            if (bitsLeft < shift) {
                if (next < data.size) {
                    buffer = buffer shl 8
                    buffer = buffer or (data[next++].toInt() and 0xff)
                    bitsLeft += 8
                } else {
                    val pad = shift - bitsLeft
                    buffer = buffer shl pad
                    bitsLeft += pad
                }
            }
            val index = mask and (buffer shr bitsLeft - shift)
            bitsLeft -= shift
            result.append(digits[index])
        }
        return result.toString()
    }

    @Throws(CloneNotSupportedException::class)
    // enforce that this class is a singleton
    fun clone(): Any {
        throw CloneNotSupportedException()
    }

    /**
     * The type Decoding exception.
     */
    class DecodingException
    /**
     * Instantiates a new Decoding exception.
     *
     * @param message the message
     */
        (message: String) : Exception(message)

    companion object {
        // singleton

        /**
         * The Separator.
         */
        internal val SEPARATOR = "-"
        /**
         * Gets instance.
         *
         * @return the instance
         */
        internal val instance =
            Base32String("ABCDEFGHIJKLMNOPQRSTUVWXYZ234567") // RFC 4648/3548

        /**
         * Decode byte [ ].
         *
         * @param encoded the encoded
         * @return the byte [ ]
         * @throws DecodingException the decoding exception
         */
        @Throws(DecodingException::class)
        fun decode(encoded: String): ByteArray {
            return instance.decodeInternal(encoded)
        }

        /**
         * Encode string.
         *
         * @param data the data
         * @return the string
         */
        fun encode(data: ByteArray): String {
            return instance.encodeInternal(data)
        }
    }
}
