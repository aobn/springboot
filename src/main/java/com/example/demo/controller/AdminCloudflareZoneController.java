package com.example.demo.controller;

import com.example.demo.common.ApiResponse;
import com.example.demo.service.AdminCloudflareZoneService;
import com.example.demo.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Admin Cloudflare Zone Management Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/cloudflare/zones")
@RequiredArgsConstructor
public class AdminCloudflareZoneController {

    private final AdminCloudflareZoneService adminCloudflareZoneService;
    private final JwtUtil jwtUtil;

    /**
     * Initialize zone prefix pool
     */
    @PostMapping("/{zoneId}/prefix-pool/init")
    public ApiResponse<Map<String, Object>> initPrefixPool(
            @PathVariable String zoneId,
            HttpServletRequest request) {
        
        try {
            // Validate admin token
            String token = jwtUtil.getTokenFromRequest(request);
            if (!jwtUtil.validateAdminToken(token)) {
                return ApiResponse.error(403, "Admin permission required");
            }

            Map<String, Object> result = adminCloudflareZoneService.initPrefixPool(zoneId);
            return ApiResponse.success("Prefix pool initialized successfully", result);
            
        } catch (Exception e) {
            log.error("Failed to initialize prefix pool: zoneId={}, error={}", zoneId, e.getMessage(), e);
            return ApiResponse.error(500, "Failed to initialize prefix pool: " + e.getMessage());
        }
    }

    /**
     * Reset zone prefix pool
     */
    @PostMapping("/{zoneId}/prefix-pool/reset")
    public ApiResponse<Map<String, Object>> resetPrefixPool(
            @PathVariable String zoneId,
            HttpServletRequest request) {
        
        try {
            // Validate admin token
            String token = jwtUtil.getTokenFromRequest(request);
            if (!jwtUtil.validateAdminToken(token)) {
                return ApiResponse.error(403, "Admin permission required");
            }

            Map<String, Object> result = adminCloudflareZoneService.resetPrefixPool(zoneId);
            return ApiResponse.success("Prefix pool reset successfully", result);
            
        } catch (Exception e) {
            log.error("Failed to reset prefix pool: zoneId={}, error={}", zoneId, e.getMessage(), e);
            return ApiResponse.error(500, "Failed to reset prefix pool: " + e.getMessage());
        }
    }

    /**
     * Get zone prefix pool status
     */
    @GetMapping("/{zoneId}/prefix-pool/status")
    public ApiResponse<Map<String, Object>> getPrefixPoolStatus(
            @PathVariable String zoneId,
            HttpServletRequest request) {
        
        try {
            // Validate admin token
            String token = jwtUtil.getTokenFromRequest(request);
            if (!jwtUtil.validateAdminToken(token)) {
                return ApiResponse.error(403, "Admin permission required");
            }

            Map<String, Object> result = adminCloudflareZoneService.getPrefixPoolStatus(zoneId);
            return ApiResponse.success("Get prefix pool status successfully", result);
            
        } catch (Exception e) {
            log.error("Failed to get prefix pool status: zoneId={}, error={}", zoneId, e.getMessage(), e);
            return ApiResponse.error(500, "Failed to get prefix pool status: " + e.getMessage());
        }
    }

    /**
     * Batch initialize all zone prefix pools
     */
    @PostMapping("/prefix-pool/batch-init")
    public ApiResponse<Map<String, Object>> batchInitPrefixPools(HttpServletRequest request) {
        
        try {
            // Validate admin token
            String token = jwtUtil.getTokenFromRequest(request);
            if (!jwtUtil.validateAdminToken(token)) {
                return ApiResponse.error(403, "Admin permission required");
            }

            Map<String, Object> result = adminCloudflareZoneService.batchInitPrefixPools();
            return ApiResponse.success("Batch initialize prefix pools successfully", result);
            
        } catch (Exception e) {
            log.error("Failed to batch initialize prefix pools: error={}", e.getMessage(), e);
            return ApiResponse.error(500, "Failed to batch initialize prefix pools: " + e.getMessage());
        }
    }

    /**
     * Get all zone prefix pool status
     */
    @GetMapping("/prefix-pool/status-all")
    public ApiResponse<Map<String, Object>> getAllPrefixPoolStatus(HttpServletRequest request) {
        
        try {
            // Validate admin token
            String token = jwtUtil.getTokenFromRequest(request);
            if (!jwtUtil.validateAdminToken(token)) {
                return ApiResponse.error(403, "Admin permission required");
            }

            Map<String, Object> result = adminCloudflareZoneService.getAllPrefixPoolStatus();
            return ApiResponse.success("Get all prefix pool status successfully", result);
            
        } catch (Exception e) {
            log.error("Failed to get all prefix pool status: error={}", e.getMessage(), e);
            return ApiResponse.error(500, "Failed to get all prefix pool status: " + e.getMessage());
        }
    }

    /**
     * Get zone details
     */
    @GetMapping("/{zoneId}/details")
    public ApiResponse<Map<String, Object>> getZoneDetails(
            @PathVariable String zoneId,
            HttpServletRequest request) {
        
        try {
            // Validate admin token
            String token = jwtUtil.getTokenFromRequest(request);
            if (!jwtUtil.validateAdminToken(token)) {
                return ApiResponse.error(403, "Admin permission required");
            }

            Map<String, Object> result = adminCloudflareZoneService.getZoneDetails(zoneId);
            return ApiResponse.success("Get zone details successfully", result);
            
        } catch (Exception e) {
            log.error("Failed to get zone details: zoneId={}, error={}", zoneId, e.getMessage(), e);
            return ApiResponse.error(500, "Failed to get zone details: " + e.getMessage());
        }
    }
}