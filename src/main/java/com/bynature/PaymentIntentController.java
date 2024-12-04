package com.bynature;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentIntentController {


	@PostMapping("/create-payment-intent")
	public PaymentIntentResponse createPaymentIntent(@RequestBody PaymentIntentRequest paymentIntentRequest)
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

		PaymentIntent intent =
				PaymentIntent.create(params);

		return new PaymentIntentResponse(intent.getId(),
				intent.getClientSecret());
	}
}
