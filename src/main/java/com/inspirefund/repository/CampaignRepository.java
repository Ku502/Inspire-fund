package com.inspirefund.repository;

import com.inspirefund.entity.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CampaignRepository extends JpaRepository<Campaign, Long> {
    List<Campaign> findByStatus(Campaign.Status status);
    List<Campaign> findByCategory(String category);
    List<Campaign> findByStatusOrderByCreatedAtDesc(Campaign.Status status);
}
