package com.university.scms.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Main View Controller
 * 메인 페이지 렌더링을 담당하는 컨트롤러
 */
@Controller
public class MainViewController {

    /**
     * 테스트 페이지
     */
    @GetMapping("/test")
    public String test() {
        return "test";
    }

    /**
     * 메인 페이지 (홈)
     * 
     * @return index.html 템플릿
     */
    @GetMapping("/")
    public String index() {
        return "index";
    }
    
    /**
     * 프로그램 목록 페이지
     * 
     * @return program/list.html 템플릿
     */
    @GetMapping("/programs")
    public String programList() {
        return "program/list";
    }
    
    /**
     * 프로그램 목록 페이지 (대체 경로)
     * 
     * @return program/list.html 템플릿
     */
    @GetMapping("/program/list")
    public String programListAlt() {
        return "program/list";
    }
    
    /**
     * 프로그램 신청 이력 페이지
     * 
     * @return program/history.html 템플릿
     */
    @GetMapping("/program/history")
    public String programHistory() {
        return "program/history";
    }
    
    /**
     * 프로그램 상세 페이지
     * 
     * @param id 프로그램 ID
     * @return program/detail.html 템플릿
     */
    @GetMapping("/programs/{id}")
    public String programDetail() {
        return "program/detail";
    }
    
    /**
     * 프로그램 신청 페이지
     * 
     * @return program/apply.html 템플릿
     */
    @GetMapping("/program/apply")
    public String programApply() {
        return "program/apply";
    }
    
    /**
     * 마일리지 현황 페이지
     * 
     * @return mileage/dashboard.html 템플릿
     */
    @GetMapping("/mileage")
    public String mileageDashboard() {
        return "mileage/dashboard";
    }
}
