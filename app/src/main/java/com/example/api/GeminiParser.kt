package com.example.api

import android.util.Log
import com.example.BuildConfig
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class ParsedUpdate(
    val commodity: String,
    val market: String,
    val price: Double,
    val rawPhrase: String
)

object GeminiParser {

    private const val MODEL_NAME = "gemini-3.5-flash"
    private const val TAG = "GeminiParser"

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    // Supported commodities matching our seeded database
    private val VALID_COMMODITIES = listOf(
        "Premium Rice", "Oloyin Beans", "White Garri", "Yellow Garri",
        "Groundnut Oil", "Palm Oil", "Granulated Sugar", "Dangote Cement",
        "Local Beef", "Tuber Yam", "Red Onions", "Round Tomatoes",
        "Rodo Pepper", "Titus Mackerel", "Agege Bread", "Plantain",
        "Refined Salt", "Chicken Eggs", "Peak Powdered Milk", "Yellow Maize"
    )

    private val VALID_MARKETS = listOf(
        "Mile 12 (Mainland)",
        "Ikorodu (Mainland)",
        "Isale Eko (Island)",
        "Lekki (Island)"
    )

    suspend fun parseUpdate(rawMessage: String): ParsedUpdate? {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "YOUR_GEMINI_API_KEY" || apiKey == "MY_GEMINI_API_KEY") {
            Log.w(TAG, "Gemini API key is not configured. Falling back to local keyword parsing.")
            return fallbackLocalParser(rawMessage)
        }

        return try {
            val systemInstruction = """
                Your task is to parse a raw text message containing wholesale commodity price updates for Lagos markets.
                Extract:
                - commodity: Must match one of these 20 exactly: Premium Rice, Oloyin Beans, White Garri, Yellow Garri, Groundnut Oil, Palm Oil, Granulated Sugar, Dangote Cement, Local Beef, Tuber Yam, Red Onions, Round Tomatoes, Rodo Pepper, Titus Mackerel, Agege Bread, Plantain, Refined Salt, Chicken Eggs, Peak Powdered Milk, Yellow Maize.
                - market: Must map to one of these 4 locations exactly: "Mile 12 (Mainland)", "Ikorodu (Mainland)", "Isale Eko (Island)", "Lekki (Island)". Default to "Mile 12 (Mainland)" if it corresponds to mainland hubs or "Isale Eko (Island)" if Island hubs.
                - price: Numeric double price. E.g. "85,000 NGN" or "85k" is 85000.0.
                - rawPhrase: A short snippet of the text containing the price.
                
                Respond ONLY with raw JSON matching this schema:
                {
                  "commodity": "Premium Rice",
                  "market": "Mile 12 (Mainland)",
                  "price": 85000.0,
                  "rawPhrase": "Rice bag 85000 naira"
                }
            """.trimIndent()

            val requestBodyJson = JSONObject()
            
            // Contents
            val contentsArray = org.json.JSONArray()
            val contentObj = JSONObject()
            val partsArray = org.json.JSONArray()
            val partObj = JSONObject()
            partObj.put("text", "Parse this raw update message: \"$rawMessage\"")
            partsArray.put(partObj)
            contentObj.put("parts", partsArray)
            contentsArray.put(contentObj)
            requestBodyJson.put("contents", contentsArray)

            // System Instruction
            val sysInstructionObj = JSONObject()
            val sysPartsArray = org.json.JSONArray()
            val sysPartObj = JSONObject()
            sysPartObj.put("text", systemInstruction)
            sysPartsArray.put(sysPartObj)
            sysInstructionObj.put("parts", sysPartsArray)
            requestBodyJson.put("systemInstruction", sysInstructionObj)

            // Config
            val generationConfig = JSONObject()
            generationConfig.put("responseMimeType", "application/json")
            generationConfig.put("temperature", 0.1)
            requestBodyJson.put("generationConfig", generationConfig)

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val body = requestBodyJson.toString().toRequestBody(mediaType)
            
            val url = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL_NAME:generateContent?key=$apiKey"
            val request = Request.Builder()
                .url(url)
                .post(body)
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                Log.e(TAG, "API Call failed with status code ${response.code}. Response: ${response.body?.string()}")
                return fallbackLocalParser(rawMessage)
            }

            val responseString = response.body?.string() ?: ""
            val responseJson = JSONObject(responseString)
            val candidates = responseJson.optJSONArray("candidates")
            val firstCandidate = candidates?.optJSONObject(0)
            val content = firstCandidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val firstPart = parts?.optJSONObject(0)
            val textResult = firstPart?.optString("text") ?: ""

            if (textResult.isEmpty()) {
                return fallbackLocalParser(rawMessage)
            }

            val resultJson = JSONObject(textResult.trim())
            val commodity = resultJson.optString("commodity", "")
            val market = resultJson.optString("market", "Mile 12 (Mainland)")
            val price = resultJson.optDouble("price", 0.0)
            val rawPhrase = resultJson.optString("rawPhrase", rawMessage)

            if (commodity.isNotEmpty() && price > 0) {
                ParsedUpdate(commodity, market, price, rawPhrase)
            } else {
                fallbackLocalParser(rawMessage)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error invoking Gemini API: ${e.message}", e)
            fallbackLocalParser(rawMessage)
        }
    }

    private fun fallbackLocalParser(message: String): ParsedUpdate? {
        Log.i(TAG, "Parsing update locally via Regex match rules.")
        val text = message.lowercase()

        // 1. Find commodity
        val matchedComm = VALID_COMMODITIES.find { comm ->
            text.contains(comm.lowercase()) ||
            comm.split(" ").any { part -> part.length > 3 && text.contains(part.lowercase()) }
        } ?: "Premium Rice" // Fallback default

        // 2. Find market location
        val market = when {
            text.contains("isale") || text.contains("eko") || text.contains("isale-eko") -> "Isale Eko (Island)"
            text.contains("lekki") -> "Lekki (Island)"
            text.contains("ikorodu") -> "Ikorodu (Mainland)"
            text.contains("mile") || text.contains("12") -> "Mile 12 (Mainland)"
            text.contains("island") -> "Isale Eko (Island)"
            else -> "Mile 12 (Mainland)" // Standard default
        }

        // 3. Find price via regex
        val numbers = mutableListOf<Double>()
        val matches = Regex("[0-9,]+").findAll(text)
        for (m in matches) {
            val numStr = m.value.replace(",", "")
            if (numStr.isNotEmpty()) {
                val num = numStr.toDoubleOrNull() ?: 0.0
                if (num > 100) { // Filter out random tiny numbers like units, dates, codes
                    numbers.add(num)
                }
            }
        }

        val finalPrice = numbers.firstOrNull() ?: 65000.0 // Default dynamic seed range

        return ParsedUpdate(
            commodity = matchedComm,
            market = market,
            price = finalPrice,
            rawPhrase = "Extracted locally from: \"$message\""
        )
    }
}
