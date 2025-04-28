package com.bynature.adapters.in.web.payment;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class PaymentIntentController {


	@PostMapping("/create-payment-intent")
	public ResponseEntity<String> createPaymentIntent(@Valid @RequestBody PaymentIntentRequest paymentIntentRequest)
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
