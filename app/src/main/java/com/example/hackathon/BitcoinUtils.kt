package com.example.hackathon

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import com.example.hackathon.model.WalletResponse
import okhttp3.*
import java.util.concurrent.TimeUnit

const val TIMEOUT = 30L

class BitcoinUtils {
    val BASE_URL = "http://cryptowalletservice.herokuapp.com"
    val WALLET ="/wallet"
    val PAYMENT ="/payment"
    var client = OkHttpClient().newBuilder()
        .connectTimeout(TIMEOUT , TimeUnit.SECONDS)
        .readTimeout(TIMEOUT, TimeUnit.SECONDS)
        .build()

    abstract class WalletBaseTask(context: Context, userId: String) :AsyncTask<String, String, WalletResponse>() {
        val userId = userId
        val walletResponse = WalletResponse()

        open fun showPreDialog(){}

        override fun doInBackground(vararg params: String?): WalletResponse {
            return walletResponse
        }
    }

    class CreateWalletTask(context: Context, userId: String) : BitcoinUtils.WalletBaseTask(context, userId) {

        override fun showPreDialog() {

        }

        override fun onPreExecute() {
            println("towa creating wallet")
        }

        override fun doInBackground(vararg params: String?): WalletResponse{
            try {
                val response = BitcoinUtils().createWallet(userId)

                walletResponse.statusCode = response.code()

                if (response.isSuccessful) {
                    if (response.code() == 200) {
                        walletResponse.responseString = response.body()?.string().toString()
                    }
                }
            } catch (e:Exception) {

            }
            return walletResponse
        }

        override fun onPostExecute(result: WalletResponse?) {
            println("towa create wallet result: code = " + result?.statusCode + " towa string = " + result?.responseString)
        }
    }

    fun createWallet(userName: String) : Response {
        val userBody = "{\"userId\": \"" + userName + "\"}"

        System.out.println("towa user Body = " + userBody)

        val request: Request = Request.Builder()
            .header("X-Client-Type", "Android")
            .url(BASE_URL + WALLET)
            .post(
                RequestBody
                    .create(
                        MediaType
                            .parse("application/json"),
                        userBody
                    )
            )
            .build()
        return client.newCall(request).execute()
    }

    fun makePayment(address: String, amount: String) {

    }

    fun getWallet(userId:String) : Response{

        val request: Request = Request.Builder()
            .header("X-Client-Type", "Android")
            .url(BASE_URL + WALLET + "/" + userId)
            .get()
            .build()
        return client.newCall(request).execute()
    }

}