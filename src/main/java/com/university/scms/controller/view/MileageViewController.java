package com.university.scms.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Mileage View Controller
 * 마일리지 관련 화면 렌더링을 담당하는 컨트롤러
 * 
 * @since 2025-11-04
 */
@Controller
@RequestMapping("/mileage")
public class MileageViewController {

    /**
     * 마일리지 대시보드 페이지
     * 
     * @return mileage/dashboard.html 템플릿
     */
    @GetMapping("")
    public String dashboard() {
        return "mileage/dashboard";
    }
}
