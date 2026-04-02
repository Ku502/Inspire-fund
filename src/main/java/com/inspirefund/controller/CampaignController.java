package com.inspirefund.controller;

import com.inspirefund.entity.*;
import com.inspirefund.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/campaigns")
@RequiredArgsConstructor
public class CampaignController {

    private final CampaignRepository campaignRepository;

    // PUBLIC: list all active campaigns
    @GetMapping
    public ResponseEntity<List<Campaign>> getActiveCampaigns(
            @RequestParam(required = false) String category) {
        List<Campaign> campaigns = category != null
            ? campaignRepository.findByCategory(category)
            : campaignRepository.findByStatusOrderByCreatedAtDesc(Campaign.Status.ACTIVE);
        return ResponseEntity.ok(campaigns);
    }

    // PUBLIC: get one campaign
    @GetMapping("/{id}")
    public ResponseEntity<Campaign> getCampaign(@PathVariable Long id) {
        return campaignRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // ADMIN: create campaign
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Campaign> createCampaign(
            @RequestBody Campaign campaign,
            @AuthenticationPrincipal User admin) {
        campaign.setCreatedBy(admin);
        campaign.setStatus(Campaign.Status.ACTIVE);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(campaignRepository.save(campaign));
    }

    // ADMIN: update campaign
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Campaign> updateCampaign(
            @PathVariable Long id, @RequestBody Campaign updated) {
        return campaignRepository.findById(id).map(c -> {
            c.setTitle(updated.getTitle());
            c.setDescription(updated.getDescription());
            c.setGoal(updated.getGoal());
            c.setStatus(updated.getStatus());
            return ResponseEntity.ok(campaignRepository.save(c));
        }).orElse(ResponseEntity.notFound().build());
    }

    // ADMIN: close campaign
    @PatchMapping("/{id}/close")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> closeCampaign(@PathVariable Long id) {
        return campaignRepository.findById(id).map(c -> {
            c.setStatus(Campaign.Status.CLOSED);
            campaignRepository.save(c);
            return ResponseEntity.ok(Map.of("message", "Campaign closed."));
        }).orElse(ResponseEntity.notFound().build());
    }

    // ADMIN: all campaigns (including pending)
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Campaign>> getAllCampaigns() {
        return ResponseEntity.ok(campaignRepository.findAll());
    }
}
