package com.inspirefund.repository;

import com.inspirefund.entity.Donation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface DonationRepository extends JpaRepository<Donation, Long> {
    List<Donation> findByCampaignIdOrderByCreatedAtDesc(Long campaignId);
    List<Donation> findByUserIdOrderByCreatedAtDesc(Long userId);
    Optional<Donation> findByRazorpayOrderId(String razorpayOrderId);
}
