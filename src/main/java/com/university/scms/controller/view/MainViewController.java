package com.university.scms.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Main View Controller
 * 메인 페이지 및 공통 페이지 렌더링을 담당하는 컨트롤러
 * 
 * @since 2025-11-04
 */
@Controller
public class MainViewController {

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
     * 테스트 페이지
     * 
     * @return test.html 템플릿
     */
    @GetMapping("/test")
    public String test() {
        return "test";
    }
}
