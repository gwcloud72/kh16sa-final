package com.kh.finalproject.restcontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.finalproject.dto.QuizLogDto;
import com.kh.finalproject.service.QuizLogService;
import com.kh.finalproject.vo.RankVO;

@CrossOrigin
@RestController
@RequestMapping("/quiz/log")
public class QuizLogRestController {
	
	@Autowired
	private QuizLogService quizLogService;
	
	//퀴즈 기록 등록
	@PostMapping("/submit")
	public int submitQuiz(
			//TokenVO에서 loginId 추출, 
			@RequestBody List<QuizLogDto> logList) {
		
		//Token에서 userId 추출해서 logList의 모든 객체에 세팅하는 반복문 필요
		// for(QuizLogDto log : logList) { log.setQuizLogMemberId(TokenVO.getLoginId()); }
		
		return quizLogService.submitQuizSession(logList);
	}
	
	//마이페이지 조회
	@GetMapping("/mypage")
	public List<QuizLogDto> getMyLogs(
			//TokenVO에서 loginId 추출
			) {
		
		String memberId = "TokenVO.getLoginId()";
		return quizLogService.myQuizLogList(memberId);
	}
	
	//퀴즈 기록 상세 정보 조회
	@GetMapping("/{quizLogId}")
	public QuizLogDto detail(@PathVariable long quizLogId) {
		return quizLogService.quizLogDetail(quizLogId);
	}
	
	
	//내 랭킹(점수) 확인
	@GetMapping("/score")
	public int getMyScore(
			// TokenVO에서 loginId 추출 예정
			) {
		String memberId = "test01";
		return quizLogService.getMyScore(memberId);
	}
	
	//전체 랭킹 확인
	@GetMapping("/ranking")
	public List<RankVO> getRanking() {
		return quizLogService.getRankingList();
	}
	
	//어떤 사람이 해당 문제를 풀었는지 확인(관리자용)
	@GetMapping("/quiz/{quizLogQuizId}")
	public List<QuizLogDto> getLogsByQuiz(@PathVariable long quizLogQuizId) {
		//관리자 권한 확인 로직 추가 예정
		return quizLogService.quizLogList(quizLogQuizId);
	}
	
}





