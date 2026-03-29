package com.inspirefund.controller;

import com.inspirefund.entity.*;
import com.inspirefund.repository.*;
import com.razorpay.*;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequestMapping("/api/donations")
@RequiredArgsConstructor
public class DonationController {

    private final DonationRepository donationRepository;
    private final CampaignRepository campaignRepository;

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    // Step 1: Create Razorpay order and save pending donation
    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(
            @RequestBody Map<String, Object> req,
            @AuthenticationPrincipal User user) {

        Long campaignId = Long.valueOf(req.get("campaignId").toString());
        BigDecimal amount = new BigDecimal(req.get("amount").toString());

        Campaign campaign = campaignRepository.findById(campaignId)
            .orElseThrow(() -> new RuntimeException("Campaign not found"));

        try {
            RazorpayClient client = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
            JSONObject options = new JSONObject();
            options.put("amount", amount.multiply(BigDecimal.valueOf(100)).intValue()); // paise
            options.put("currency", "INR");
            options.put("receipt", "inspirefund_" + System.currentTimeMillis());

            Order razorpayOrder = client.orders.create(options);
            String orderId = razorpayOrder.get("id");

            // Save donation as PENDING
            Donation donation = Donation.builder()
                .user(user)
                .campaign(campaign)
                .amount(amount)
                .razorpayOrderId(orderId)
                .paymentStatus(Donation.PaymentStatus.PENDING)
                .anonymous(false)
                .build();
            donationRepository.save(donation);

            return ResponseEntity.ok(Map.of(
                "razorpayOrderId", orderId,
                "amount", options.get("amount"),
                "currency", "INR",
                "keyId", razorpayKeyId
            ));

        } catch (RazorpayException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Payment gateway error: " + e.getMessage()));
        }
    }

    // Step 2: Verify Razorpay signature and update donation + campaign
    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(
            @RequestBody Map<String, String> req,
            @AuthenticationPrincipal User user) {

        String orderId   = req.get("razorpay_order_id");
        String paymentId = req.get("razorpay_payment_id");
        String signature = req.get("razorpay_signature");

        try {
            // HMAC-SHA256 signature verification
            String payload = orderId + "|" + paymentId;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(razorpayKeySecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            String expectedSignature = bytesToHex(hash);

            if (!expectedSignature.equals(signature)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Payment signature verification failed."));
            }

            // Update donation record
            Donation donation = donationRepository.findByRazorpayOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Donation not found"));

            donation.setRazorpayPaymentId(paymentId);
            donation.setRazorpaySignature(signature);
            donation.setPaymentStatus(Donation.PaymentStatus.SUCCESS);
            donationRepository.save(donation);

            // Update campaign raised amount and donor count
            Campaign campaign = donation.getCampaign();
            campaign.setRaised(campaign.getRaised().add(donation.getAmount()));
            campaign.setDonorCount(campaign.getDonorCount() + 1);
            campaignRepository.save(campaign);

            return ResponseEntity.ok(Map.of(
                "message", "Payment verified. Thank you for your donation!",
                "donationId", donation.getId()
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Verification error: " + e.getMessage()));
        }
    }

    // Get donations for a campaign
    @GetMapping("/campaign/{id}")
    public ResponseEntity<List<Donation>> getDonationsForCampaign(@PathVariable Long id) {
        return ResponseEntity.ok(donationRepository.findByCampaignIdOrderByCreatedAtDesc(id));
    }

    // Get current user's donation history
    @GetMapping("/my")
    public ResponseEntity<List<Donation>> getMyDonations(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(donationRepository.findByUserIdOrderByCreatedAtDesc(user.getId()));
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}
