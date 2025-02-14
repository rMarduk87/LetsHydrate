package rpt.tool.mementobibere.utils.helpers

import android.content.Context;
import android.util.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import rpt.tool.mementobibere.utils.log.e


class JsonHelper
    (var mContext: Context) {
    fun callApi(url: String?, params: List<NameValuePair?>?): String {
        val str = StringBuilder()
        val client: HttpClient = DefaultHttpClient()
        val httpPost: HttpPost = HttpPost(url)
        val response: HttpResponse
        val statusLine: StatusLine

        try {
            httpPost.entity = UrlEncodedFormEntity(params, "utf-8")
            response = client.execute(httpPost)
            statusLine = response.statusLine

            val statusCode: Int = statusLine.statusCode

            if (statusCode == 200)  // Status OK
            {
                val entity: HttpEntity = response.entity
                val content: InputStream = entity.content
                val reader = BufferedReader(InputStreamReader(content, "utf-8"), 8)
                var line: String?
                while ((reader.readLine().also { line = it }) != null) {
                    str.append(line)
                }
            } else {
                val e = Exception("Failed to download result..")
                e.message?.let { e(Throwable(e), it) }
            }
        } catch (e: ClientProtocolException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return str.toString()
    }

    fun callApi(url: String?): String {
        val str = StringBuilder()
        val client: HttpClient = DefaultHttpClient()
        val httpPost: HttpPost = HttpPost(url)
        val response: HttpResponse
        val statusLine: StatusLine

        try {
            //httpPost.setEntity(new UrlEncodedFormEntity(params,"utf-8"));
            response = client.execute(httpPost)
            statusLine = response.statusLine

            val statusCode: Int = statusLine.statusCode

            if (statusCode == 200)  // Status OK
            {
                val entity: HttpEntity = response.entity
                val content: InputStream = entity.content
                val reader = BufferedReader(InputStreamReader(content, "utf-8"), 8)
                var line: String?
                while ((reader.readLine().also { line = it }) != null) {
                    str.append(line)
                }
            } else {
                val e = Exception("Failed to download result..")
                e.message?.let { e(Throwable(e), it) }
            }
        } catch (e: ClientProtocolException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return str.toString()
    }

    fun callApi2(url: String?, params: List<NameValuePair?>?): String {
        val str = StringBuilder()
        var client: HttpClient = DefaultHttpClient()
        val httpPost: HttpPost = HttpPost(url)

        try {
            var sslFactory: SSLSocketFactory? = null
            sslFactory = SSLSocketHelper(null)

            sslFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER)

            // Enable HTTP parameters
            val hparams: HttpParams = BasicHttpParams()
            HttpProtocolParams.setVersion(hparams, HttpVersion.HTTP_1_1)
            HttpProtocolParams.setContentCharset(hparams, HTTP.UTF_8)

            // Register the HTTP and HTTPS Protocols. For HTTPS, register our custom SSL Factory object.
            val registry: SchemeRegistry = SchemeRegistry()
            registry.register(Scheme("http", PlainSocketFactory.getSocketFactory(), 80))
            registry.register(Scheme("https", sslFactory, 443))

            // Create a new connection manager using the newly created registry and then create a new HTTP client
            // using this connection manager
            val ccm: ClientConnectionManager = ThreadSafeClientConnManager(hparams, registry)
            client = DefaultHttpClient(ccm, hparams)

            httpPost.entity = UrlEncodedFormEntity(params, "utf-8")
            val response: HttpResponse = client.execute(httpPost)
            val statusLine: StatusLine = response.statusLine
            val statusCode: Int = statusLine.statusCode
            if (statusCode == 200) { // Status OK
                val entity: HttpEntity = response.entity
                val content: InputStream = entity.content
                val reader = BufferedReader(
                    InputStreamReader(content)
                )
                var line: String?
                while ((reader.readLine().also { line = it }) != null) {
                    str.append(line)
                }
            } else {
                val e = Exception("Failed to download result...$statusCode")
                e.message?.let { e(Throwable(e), it) }
            }
        } catch (e: ClientProtocolException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: KeyManagementException) {
            e.printStackTrace()
        } catch (e: UnrecoverableKeyException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: KeyStoreException) {
            e.printStackTrace()
        }

        return str.toString()
    }

    fun callApi2(url: String?): String {
        val str = StringBuilder()
        var client: HttpClient = DefaultHttpClient()
        val httpPost: HttpPost = HttpPost(url)

        try {
            var sslFactory: SSLSocketFactory? = null
            sslFactory = SSLSocketHelper(null)

            sslFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER)

            // Enable HTTP parameters
            val hparams: HttpParams = BasicHttpParams()
            HttpProtocolParams.setVersion(hparams, HttpVersion.HTTP_1_1)
            HttpProtocolParams.setContentCharset(hparams, HTTP.UTF_8)

            // Register the HTTP and HTTPS Protocols. For HTTPS, register our custom SSL Factory object.
            val registry: SchemeRegistry = SchemeRegistry()
            registry.register(Scheme("http", PlainSocketFactory.getSocketFactory(), 80))
            registry.register(Scheme("https", sslFactory, 443))

            // Create a new connection manager using the newly created registry and then create a new HTTP client
            // using this connection manager
            val ccm: ClientConnectionManager = ThreadSafeClientConnManager(hparams, registry)
            client = DefaultHttpClient(ccm, hparams)

            //httpPost.setEntity(new UrlEncodedFormEntity(params, "utf-8"));
            val response: HttpResponse = client.execute(httpPost)
            val statusLine: StatusLine = response.statusLine
            val statusCode: Int = statusLine.statusCode
            if (statusCode == 200) { // Status OK
                val entity: HttpEntity = response.entity
                val content: InputStream = entity.content
                val reader = BufferedReader(
                    InputStreamReader(content)
                )
                var line: String?
                while ((reader.readLine().also { line = it }) != null) {
                    str.append(line)
                }
            } else {
                val e = Exception("Failed to download result...$statusCode")
                e.message?.let { e(Throwable(e), it) }
            }
        } catch (e: ClientProtocolException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: KeyManagementException) {
            e.printStackTrace()
        } catch (e: UnrecoverableKeyException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: KeyStoreException) {
            e.printStackTrace()
        }

        return str.toString()
    }
}