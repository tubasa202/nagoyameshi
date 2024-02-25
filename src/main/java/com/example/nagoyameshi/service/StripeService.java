package com.example.nagoyameshi.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class StripeService { // クラス名をServiceからStripeServiceへ変更してみました
	
	  @Value("${stripe.api-key}")
	     private String stripeApiKey;

    private static final long SUBSCRIPTION_AMOUNT = 300L; // 単位は最小通貨単位（この場合は円）

    public String createStripeSession(HttpServletRequest httpServletRequest) {
        Stripe.apiKey = stripeApiKey;
        String requestUrl = httpServletRequest.getRequestURL().toString();

        SessionCreateParams params =
                SessionCreateParams.builder()
                        .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                        .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                        .addLineItem(SessionCreateParams.LineItem.builder() // サブスクリプションの設定
                                .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                        .setCurrency("jpy")
                                        .setUnitAmount(SUBSCRIPTION_AMOUNT)
                                        .setRecurring(SessionCreateParams.LineItem.PriceData.Recurring.builder()
                                                .setInterval(SessionCreateParams.LineItem.PriceData.Recurring.Interval.MONTH)
                                                .setIntervalCount(1L) // 1ヶ月ごとのサブスクリプション
                                                .build())
                                        .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                .setName("サブスクリプションサービス")
                                                .build())
                                        .build())
                                .setQuantity(1L)
                                .build())
                        .setSuccessUrl(requestUrl.replaceAll("/primes", "/success.html")) // 成功時のURL
                        .setCancelUrl(requestUrl.replace("/primes/delete", "/cancel.html")) // キャンセル時のURL
                        .build();

        try {
            Session session = Session.create(params);
            return session.getId();
        } catch (StripeException e) {
            e.printStackTrace();
            return "";
        }
    }
}