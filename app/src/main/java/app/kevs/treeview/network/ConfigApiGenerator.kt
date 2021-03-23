package app.kevs.treeview.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ConfigApiGenerator{
    companion object {

        private val httpClient = OkHttpClient.Builder()
        private const val CONFIG_BASE_URL: String = "https://tree-api-config.getsandbox.com/"
        private const val MIGRATE_BASE_URL: String = "https://kevinctestfunc1.azurewebsites.net/api/"

        private val configBuilder: Retrofit.Builder = Retrofit.Builder().baseUrl(CONFIG_BASE_URL).addConverterFactory(GsonConverterFactory.create())
        private val migrateBuilder: Retrofit.Builder = Retrofit.Builder().baseUrl(MIGRATE_BASE_URL).addConverterFactory(GsonConverterFactory.create())

        private var configRetrofit: Retrofit = configBuilder.build()
        private var migrateRetrofit: Retrofit = migrateBuilder.build()

        fun <S> createServiceConfigApi(serviceClass: Class<S>): S {
            configBuilder.client(httpClient.build())
            configRetrofit = configBuilder.build()
            return configRetrofit.create(serviceClass)
        }

        fun <S> createServiceMigrateApi(serviceClass: Class<S>): S {
            migrateBuilder.client(httpClient.build())
            migrateRetrofit = migrateBuilder.build()
            return migrateRetrofit.create(serviceClass)
        }
    }
}