package com.bynature.adapters.in.web.stripe;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentIntentController {


	@PostMapping("/create-payment-intent")
	public ResponseEntity<String> createPaymentIntent(@RequestBody PaymentIntentRequest paymentIntentRequest)
			throws StripeException {
		PaymentIntentCreateParams params =
				PaymentIntentCreateParams.builder()
						.setAmount(paymentIntentRequest.getAmount() * 100L)
						.setReceiptEmail(paymentIntentRequest.getEmail())
						.putMetadata("productName", paymentIntentRequest.getProductName())
						.putMetadata("orderId", paymentIntentRequest.getOrderId().toString()) // Ajouter l'ID de la commande
						.setCurrency("EUR")
						.setAutomaticPaymentMethods(
								PaymentIntentCreateParams
										.AutomaticPaymentMethods
										.builder()
										.setEnabled(true)
										.build()
						)
						.build();

		return ResponseEntity.ok(PaymentIntent.create(params).toJson());

	}
}
