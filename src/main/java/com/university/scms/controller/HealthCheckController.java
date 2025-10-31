package com.university.scms.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 헬스 체크 컨트롤러
 * 서버 상태를 확인하는 API를 제공합니다.
 */
@RestController
@RequestMapping("/api/health")
public class HealthCheckController {

    /**
     * 서버 상태 확인
     * GET /api/health
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now());
        response.put("message", "SCMS API is running");
        
        return ResponseEntity.ok(response);
    }
}
