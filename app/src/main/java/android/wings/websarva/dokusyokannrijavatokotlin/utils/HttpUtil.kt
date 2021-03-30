package android.wings.websarva.dokusyokannrijavatokotlin.utils


import okhttp3.OkHttpClient
import okhttp3.Request

class HttpUtil {

    /**
     * http接続用メソッド
     * @param url 接続先URL
     * @return 接続結果
     */
    fun httpGET(url: String): String? {
        val request = Request.Builder().url(url).build()
        val response = HttpClient.instance.newCall(request).execute()
        return response.body?.string()
    }
}

object HttpClient {
    // OKHttp3はシングルトン
    val instance = OkHttpClient();
}