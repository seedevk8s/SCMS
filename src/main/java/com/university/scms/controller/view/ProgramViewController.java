package com.university.scms.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Program View Controller
 * 비교과 프로그램 관련 화면 렌더링을 담당하는 컨트롤러
 * 
 * @since 2025-11-04
 */
@Controller
public class ProgramViewController {

    /**
     * 프로그램 목록 페이지
     * 
     * @return program/list.html 템플릿
     */
    @GetMapping("/programs")
    public String list() {
        return "program/list";
    }
    
    /**
     * 프로그램 목록 페이지 (대체 경로)
     * 
     * @return program/list.html 템플릿
     */
    @GetMapping("/program/list")
    public String listAlt() {
        return "program/list";
    }
    
    /**
     * 프로그램 상세 페이지
     * 
     * @param id 프로그램 ID
     * @return program/detail.html 템플릿
     */
    @GetMapping("/programs/{id}")
    public String detail(@PathVariable Long id) {
        return "program/detail";
    }
    
    /**
     * 프로그램 신청 페이지
     * 
     * @return program/apply.html 템플릿
     */
    @GetMapping("/program/apply")
    public String apply() {
        return "program/apply";
    }
    
    /**
     * 프로그램 신청 이력 페이지
     * 
     * @return program/history.html 템플릿
     */
    @GetMapping("/program/history")
    public String history() {
        return "program/history";
    }
}
