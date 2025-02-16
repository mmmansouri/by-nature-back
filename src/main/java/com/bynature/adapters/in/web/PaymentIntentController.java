package com.bynature.adapters.in.web;

import com.bynature.adapters.in.web.dto.request.PaymentIntentRequest;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentIntentController {


	@PostMapping("/create-payment-intent")
	public String createPaymentIntent(@RequestBody PaymentIntentRequest paymentIntentRequest)
			throws StripeException {
		PaymentIntentCreateParams params =
				PaymentIntentCreateParams.builder()
						.setAmount(paymentIntentRequest.getAmount() * 100L)
						.putMetadata("productName",
								paymentIntentRequest.getProductName())
						.setCurrency("EUR")
						.setAutomaticPaymentMethods(
								PaymentIntentCreateParams
										.AutomaticPaymentMethods
										.builder()
										.setEnabled(true)
										.build()
						)
						.build();

		return PaymentIntent.create(params).toJson();

	}
}
