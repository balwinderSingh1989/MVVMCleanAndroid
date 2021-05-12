package com.sample.core.data.remote.factory.networkprovider

import com.sample.core.BuildConfig
import com.sample.core.data.remote.factory.auth.Token
import com.sample.core.di.scope.PerApplication
import com.sample.core.domain.remote.exception.*
import com.sample.core.utility.extensions.isProdFlavor
import com.sample.core.utility.extensions.safeGet
import com.sample.core.utility.logger.AppLogger
import com.sample.core.utility.manager.SessionManager
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.net.HttpURLConnection
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.net.ssl.*


@PerApplication
class NetworkProvider @Inject constructor(
    private val token: Token,
    private val sessionManager: SessionManager,
    private val endPoint: String
) {

    companion object {
        private const val TAG = "NetworkProvider"

        private const val HEADER_CONTENT_TYPE = "Content-Type"
        private const val HEADER_AUTH_TYPE = "Authorization"
        private const val CONNECT_TIME_OUT = 120L
        private const val READ_TIME_OUT = 120L

        private fun okhttp3.Response?.safeClose() {
            this?.let {
                try {
                    it.close()
                } catch (e: Exception) {
                    //ignore
                }
            }
        }

    }

    private var retrofit: Retrofit

    init {
        retrofit = Retrofit.Builder()
            .baseUrl(endPoint)
            .client(makeDefaultOkHttpClient())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun <T> create(serviceClass: Class<T>): T = retrofit.create(serviceClass)


    fun makeDefaultOkHttpClient(): OkHttpClient {
        val builder: OkHttpClient.Builder = OkHttpClient.Builder()
        builder.apply {
            addNetworkInterceptor(makeLoggingInterceptor())
            addInterceptor(makeHeaderInterceptor())
            addInterceptor(makeResponseInterceptor())
            connectTimeout(CONNECT_TIME_OUT, TimeUnit.SECONDS)
            readTimeout(READ_TIME_OUT, TimeUnit.SECONDS)
            cookieJar(AppCookieJar())
        }

        if (!isProdFlavor()) {
            builder
                .sslSocketFactory(
                    getInsecureSSLSocketFactory()
                )
                .hostnameVerifier(
                    getInsecureHostNameVerifier()
                )
        } else {
            builder.certificatePinner(getCertificatePinner())
        }

        return builder.build()

    }

    private fun makeLoggingInterceptor(): HttpLoggingInterceptor {
        val loggingInterceptor = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG) {
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        } else {
            loggingInterceptor.level = HttpLoggingInterceptor.Level.NONE
        }
        return loggingInterceptor
    }


    private fun makeHeaderInterceptor(): Interceptor {

        return Interceptor {
            val requestBuilder = it.request()
                .newBuilder()
                .addHeader(HEADER_CONTENT_TYPE, "application/json")
                .addHeader(HEADER_AUTH_TYPE, token.getToken())
            it.proceed(requestBuilder.build())
        }
    }


    private fun makeResponseInterceptor(): Interceptor {

        return Interceptor {

            var response: Response? = null
            try {

                response = it.proceed(it.request()) as Response

                when (response.code()) {

                    HttpURLConnection.HTTP_UNAVAILABLE -> {
                        throw ServerNotAvailableException("Server not available , Please try again later")
                    }

                    HttpURLConnection.HTTP_NOT_MODIFIED -> {
                        AppLogger.d(TAG, "HTTP NOT MODIFIED")
                        throw NoContentException("No content")
                    }

                    HttpURLConnection.HTTP_NO_CONTENT -> {
                        throw NoContentException("No content")
                    }

                    HttpURLConnection.HTTP_PROXY_AUTH -> {
                        AppLogger.d(TAG, "Proxy required")
                        throw ProxyException(response.message().safeGet())
                    }

                    HttpURLConnection.HTTP_UNAUTHORIZED -> {
                        val errorBody = response.body()?.string().safeGet()
                        AppLogger.d(
                            TAG,
                            "Authorization Error=$errorBody"
                        )
                        throw AuthorizationException(errorBody)
                    }

                    HttpURLConnection.HTTP_FORBIDDEN -> {
                        AppLogger.e(TAG, "ON SESSION EXPIRED")
                        /**
                         * publish session expiry..
                         *
                         */
                        sessionManager.sessionExpired()
                        throw SessionExpireException("ON SESSION EXPIRED")
                    }

                    HttpURLConnection.HTTP_NOT_FOUND -> {
                        AppLogger.e(TAG, "HTTP NOT FOUND")
                        throw HTTPNotFoundException("No data found")
                    }

                }
                response

            } catch (e: Exception) {
                response?.safeClose()
                e.printStackTrace()

                when (e) {
                    is HttpException,
                    is NoContentException,
                    is ProxyException,
                    is AuthorizationException,
                    is HTTPNotFoundException,
                    is ServerNotAvailableException -> {
                        throw e
                    }
                    is SessionExpireException -> {
                        sessionManager.sessionExpired()
                        AppLogger.e(TAG, "ON SESSION EXPIRED")
                        throw SessionExpireException("ON SESSION EXPIRED")
                    }

                    else -> throw NetworkException(e)
                }
            }
        }
    }


    fun getCertificatePinner(): CertificatePinner {
        val certificate = CertificatePinner.Builder()
        return certificate.build()

    }

    @SuppressWarnings("TrustAllX509TrustManager")
    private fun getInsecureSSLSocketFactory(): SSLSocketFactory? {
        try {
            val trustAllCerts =
                arrayOf<TrustManager>(object : X509TrustManager {
                    override fun checkClientTrusted(
                        chain: Array<out X509Certificate>?,
                        authType: String?
                    ) {
                    }

                    override fun checkServerTrusted(
                        chain: Array<out X509Certificate>?,
                        authType: String?
                    ) {
                    }

                    override fun getAcceptedIssuers(): Array<X509Certificate> {
                        return arrayOf()
                    }

                })

            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, java.security.SecureRandom())

            return sslContext.socketFactory

        } catch (e: KeyManagementException) {
            e.printStackTrace()
            return null

        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            return null
        }

    }

    @SuppressWarnings("BadHostnameVerifier")
    private fun getInsecureHostNameVerifier(): HostnameVerifier =
        HostnameVerifier { _, _ -> true }


    /**
     *
     *handle cookies...backened usuallly havESe SESSION_ID in cookies
     *  and this be send along with every request
     */
    class AppCookieJar : CookieJar {
        private var cookies: List<Cookie>? = null
        override fun saveFromResponse(
            url: HttpUrl,
            cookies: List<Cookie>
        ) {
            this.cookies = cookies
        }

        override fun loadForRequest(url: HttpUrl): List<Cookie> {
            return cookies ?: arrayListOf()

        }
    }


}