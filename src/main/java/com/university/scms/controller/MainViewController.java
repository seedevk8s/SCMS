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
     * 프로그램 상세 페이지
     * 
     * @param id 프로그램 ID
     * @return program/detail.html 템플릿
     */
    @GetMapping("/programs/{id}")
    public String programDetail() {
        return "program/detail";
    }
}
