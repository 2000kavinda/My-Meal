package org.myapp.mymeal

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive



class StripeRepository(private val httpClient: HttpClient) {

    private val stripeApiUrl = "https://api.stripe.com/v1"
    private val secretKey = "sk_test_51Q0OTARxdU7kvxO5Hcckz63CIgjTvrx0PQYprfzNY4MYag4r3jlz2rw6Is74meNJXyxMkAbyPuFtle9WBGeH4baI00ryDsMene"


    // Step 1: Create PaymentIntent
    suspend fun createPaymentIntent(amount: Int, currency: String): String {
        val response: HttpResponse = httpClient.post("$stripeApiUrl/payment_intents") {
            headers {
                append(HttpHeaders.Authorization, "Bearer $secretKey")
                append(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
            }
            setBody(
                listOf(
                    "amount" to amount.toString(),
                    "currency" to currency,
                    "payment_method_types[]" to "card" // Specify card as a payment method
                ).formUrlEncode()
            )
        }

        val responseBody = Json.parseToJsonElement(response.bodyAsText()).jsonObject
        return responseBody["id"]?.jsonPrimitive?.content ?: throw Exception("Failed to create PaymentIntent")
    }

    // Step 2: Create PaymentMethod
    suspend fun createPaymentMethod(cardNumber: String, expiryDate: String, cvc: String): String {
        val (expMonth, expYear) = expiryDate.split("/") // MM/YY format

        val response: HttpResponse = httpClient.post("$stripeApiUrl/payment_methods") {
            headers {
                append(HttpHeaders.Authorization, "Bearer $secretKey")
                append(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
            }
            setBody(
                listOf(
                    "type" to "card",
                    "card[number]" to cardNumber,
                    "card[exp_month]" to expMonth,
                    "card[exp_year]" to "20$expYear", // Convert YY to YYYY
                    "card[cvc]" to cvc
                ).formUrlEncode()
            )
        }

        // Log the response body to help with debugging
        val responseBody = Json.parseToJsonElement(response.bodyAsText()).jsonObject
        println("Stripe response: $responseBody") // Print the response for debugging

        return responseBody["id"]?.jsonPrimitive?.content
            ?: throw Exception("Failed to create PaymentMethod: ${responseBody["error"]?.jsonObject?.get("message")?.jsonPrimitive?.content}")
    }


    // Step 3: Confirm PaymentIntent
    suspend fun confirmPaymentIntent(paymentIntentId: String, paymentMethodId: String): String {
        val response: HttpResponse = httpClient.post("$stripeApiUrl/payment_intents/$paymentIntentId/confirm") {
            headers {
                append(HttpHeaders.Authorization, "Bearer $secretKey")
                append(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
            }
            setBody(
                listOf(
                    "payment_method" to paymentMethodId
                ).formUrlEncode()
            )
        }

        val responseBody = Json.parseToJsonElement(response.bodyAsText()).jsonObject
        return responseBody["status"]?.jsonPrimitive?.content ?: throw Exception("Failed to confirm PaymentIntent")
    }
}
