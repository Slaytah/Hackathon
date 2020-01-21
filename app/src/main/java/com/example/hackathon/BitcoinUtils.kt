package com.example.hackathon

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import com.example.hackathon.model.WalletResponse
import okhttp3.*
import java.util.concurrent.TimeUnit


class BitcoinUtils {

    val BASE_URL = "http://cryptowalletservice.herokuapp.com"
    val WALLET ="/wallet"
    val PAYMENT ="/payment"
    var client = OkHttpClient().newBuilder()
        .connectTimeout(20 , TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()


    abstract class WalletBaseTask(context: Context, userId: String) :AsyncTask<String, String, WalletResponse>() {
        val userId = userId
        val walletResponse = WalletResponse()

        open fun showPreDialog(){}

        override fun doInBackground(vararg params: String?): WalletResponse {
            return walletResponse
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

    fun startAccountDetailActivity(context: Context, walletDTOJSON: String) {
        Intent(context, ShowAccountDetailActivity::class.java).apply {
            this.putExtra("test", walletDTOJSON)
            context.startActivity(this)
        }
    }

    fun startQRCodeActivity(context: Context, bitcoinAddress: String) {
        println("towa bitcoin address" + bitcoinAddress)
        Intent(context, ShowQRCodeActivity::class.java).apply {
            this.putExtra("bitcoinaddress", bitcoinAddress)
            context.startActivity(this)
        }
    }
}