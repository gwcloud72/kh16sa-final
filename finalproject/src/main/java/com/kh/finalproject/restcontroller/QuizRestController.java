package com.kh.finalproject.restcontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.finalproject.dto.QuizDto;
import com.kh.finalproject.service.QuizService;

@CrossOrigin
@RestController
@RequestMapping("/quiz")
public class QuizRestController {
	
	@Autowired
	private QuizService quizService;
	
	//퀴즈 등록
	//TokenVO로 사용자 조회 및 quiz_creator_id로 등록
	@PostMapping("/")
	public QuizDto insert(
			//TokenVO 추가 예정
			@RequestBody QuizDto quizDto
			) {
		//토큰에서 memberId 추출 한 후
		//quizDto.setQuizCreatorId(memberId);
		
		return quizService.registQuiz(quizDto);
	}
	
	//퀴즈 푸는 페이지
	//TokenVO로 사용자 조회
	@GetMapping("/game/{contentsId}")
	public List<QuizDto> game(
			//TokenVO 추가 예정
			@PathVariable long contentsId
			){
		
		return quizService.getQuizGame(contentsId, "TokenVO.getLoginId()");
	}
	
	//해당 영화의 퀴즈 목록 조회 구문
	//관리자 페이지로 뺄 가능성 높음
	@GetMapping("/list/{contentsId}")
	public List<QuizDto> getList(@PathVariable long contentsId) {
		//관리자 권한 체크 로직 필요
		return quizService.getQuizList(contentsId);
	}
	
	//퀴즈 상세정보 조회
	@GetMapping("/{quizId}")
	public QuizDto detail(@PathVariable long quizId) {
		return quizService.getQuizDetail(quizId);
	}
	
	//퀴즈 수정
	//TokenVO로 사용자 조회
	@PatchMapping("/")
	public boolean update(
			//TokenVO 추가 예정,
			@RequestBody QuizDto quizDto) {
		
		//토큰 ID와 quizDto.getCreatorId()가 일치하는지 확인
		return quizService.editQuiz(quizDto);
	}
	
	//퀴즈 상태 변경(BLIND, DELETED)
	//삭제 구문 대신 사용할 메소드
	//본인 확인 or 관리자인지 확인하는 구문 추가 예정
	@PatchMapping("/status")
	public boolean changeStatus(@RequestBody QuizDto quizDto) {
		// quizDto에는 quizId와 quizStatus("BLIND")가 들어있어야 함
		return quizService.changeQuizStatus(quizDto);
	}
	
	//신고 누적 횟수 변경
	//중복 클릭 불가 처리 예정
	@PatchMapping("/report/{quizId}")
	public boolean reportQuiz(@PathVariable long quizId) {
	    return quizService.reportQuiz(quizId);
	}
	
	//퀴즈 삭제
	@DeleteMapping("/{quizId}")
	public boolean delete(
			//TokenVO 추가 예정,
			@PathVariable long quizId) {
		
		//TokenVO에서 loginId와 loginLevel 추출해서
		//본인 확인 or 관리자 권한 확인 구현 예정
		
		return quizService.deleteQuiz(quizId);
	}
	
	
}





