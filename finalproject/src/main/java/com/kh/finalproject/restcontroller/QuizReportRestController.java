package com.kh.finalproject.restcontroller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.finalproject.dto.QuizReportDto;
import com.kh.finalproject.service.QuizReportService;

@CrossOrigin
@RestController
@RequestMapping("/quiz/report")
public class QuizReportRestController {
	
	@Autowired
	private QuizReportService quizReportService;
	
	//신고 등록
	@PostMapping("/")
	public String insertReport(@RequestBody QuizReportDto quizReportDto) {
		//토큰에서 loginId 꺼내기
		// quizReportDto.setQuizReportMemberId(TokenVO.getLoginId());
		
		return quizReportService.insertReport(quizReportDto);
	}
	
	//신고 전체 목록 조회 (관리자용)
	@GetMapping("/list")
	public List<QuizReportDto> getList() {
		//관리자 권한 체크 로직
		
		return quizReportService.getReportList();
	}
	
	//신고 상세 내역 조회 (관리자용)
	@GetMapping("/list/{quizId}")
	public List<QuizReportDto> getListByQuiz(@PathVariable long quizId) {
		//관리자 권한 체크 로직
		
		return quizReportService.getReportListByQuiz(quizId);
	}
	
	//신고 유형별 통계 (관리자용)
	@GetMapping("/stats/{quizId}")
	public List<Map<String, Object>> getStats(@PathVariable long quizId) {
		//관리자 권한 체크 로직
		
		return quizReportService.getReportStats(quizId);
	}
	
	//신고 삭제 (관리자용)
	@DeleteMapping("/{quizReportId}")
	public boolean deleteReport(@PathVariable long quizReportId) {
	    //관리자 권한 체크 로직
	    
	    return quizReportService.deleteReport(quizReportId);
	}
}
