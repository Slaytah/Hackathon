package com.example.hackathon.model

class WalletResponse {
    var statusCode: Int = 0
    var responseString: String = ""

    override fun toString(): String {
        return("status: " + statusCode + " resposne: " + responseString)
    }
}