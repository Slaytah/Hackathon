package com.example.hackathon

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import com.example.hackathon.model.WalletResponse
import okhttp3.*


class BitcoinUtils {

    val BASE_URL = "http://cryptowalletservice.herokuapp.com"
    val GENERATE_WALLET ="/wallet"
    var client = OkHttpClient()

    abstract class WalletBaseTask(context: Context, userId: String) :AsyncTask<String, String, WalletResponse>() {
        val userId = userId
        val walletResponse = WalletResponse()

        open fun showPreDialog(){}

        override fun doInBackground(vararg params: String?): WalletResponse {
            return walletResponse
        }
    }

    data class BalanceDTO(
        val available: Long,
        val estimated: Long)

    data class WalletDTO(val balance: BalanceDTO,
                         val address: String,
                         val userId: String)

    fun createWallet(userName: String) : Response {
        val userBody = "{\"userId\": \"" + userName + "\"}"

        System.out.println("towa user Body = " + userBody)

        val request: Request = Request.Builder()
            .header("X-Client-Type", "Android")
            .url(BASE_URL + GENERATE_WALLET)
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


    fun getWallet(userId:String) : Response{
        val request: Request = Request.Builder()
            .header("X-Client-Type", "Android")
            .url(BASE_URL + GENERATE_WALLET + "/" + userId)
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

    fun startQRCodeActivity(context: Context) {
        Intent(context, ShowQRCodeActivity::class.java).apply {
            context.startActivity(this)
        }
    }

}