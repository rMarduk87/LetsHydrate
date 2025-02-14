package rpt.tool.mementobibere.utils.helpers

import android.annotation.SuppressLint
import org.apache.http.conn.ssl.SSLSocketFactory
import java.io.IOException
import java.net.Socket
import java.net.UnknownHostException
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class SSLSocketHelper(truststore: KeyStore?) : SSLSocketFactory(null) {
    private var sslFactory: javax.net.ssl.SSLSocketFactory =
        HttpsURLConnection.getDefaultSSLSocketFactory()

    init {
        try {
            val context = SSLContext.getInstance("TLS")


            // Create a trust manager that does not validate certificate chains and simply accept all type of certificates
            val trustAllCerts = arrayOf<TrustManager>(@SuppressLint("CustomX509TrustManager")
            object : X509TrustManager {
                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return arrayOf()
                }

                @Throws(CertificateException::class)
                override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
                }

                @Throws(CertificateException::class)
                override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
                }
            }
            )


            // Initialize the socket factory
            context.init(null, trustAllCerts, SecureRandom())

            sslFactory = context.socketFactory
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Deprecated("Deprecated in Java")
    @Throws(IOException::class, UnknownHostException::class)
    override fun createSocket(socket: Socket, host: String, port: Int, autoClose: Boolean): Socket {
        return sslFactory.createSocket(socket, host, port, autoClose)
    }

    @Deprecated("Deprecated in Java")
    @Throws(IOException::class)
    override fun createSocket(): Socket {
        return sslFactory.createSocket()
    }
}