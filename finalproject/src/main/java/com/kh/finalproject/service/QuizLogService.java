package com.kh.finalproject.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.finalproject.dao.QuizLogDao;
import com.kh.finalproject.dto.QuizLogDto;
import com.kh.finalproject.vo.RankVO;

@Service
public class QuizLogService {

	@Autowired
	private QuizLogDao quizLogDao;
	
	
	@Transactional
	//퀴즈 기록 등록
	public int submitQuizSession(List<QuizLogDto> logList) {
		int correctCount = 0;
		
		for(QuizLogDto log : logList) {
			//퀴즈 기록을 개별로 저장
			quizLogDao.insert(log);
			
			//정답 개수 카운트
			if("Y".equals(log.getQuizLogIsCorrect())) {
				correctCount++;
			}
		}
		
		//correctCount에 따라 포인트 지급 로직 호출 구현 예정
		
		return correctCount; //정답 개수를 int로 반환
	}
	
	//퀴즈 기록 상세 정보 조회
	//오답노트 느낌?
	public QuizLogDto quizLogDetail(long quizLogId) {
		return quizLogDao.selectOne(quizLogId);
	}
	
	//마이페이지 조회
	public List<QuizLogDto> myQuizLogList(String quizLogMemberId){
		return quizLogDao.selectListByMember(quizLogMemberId);
	}
	
	//내 랭킹 조회
	//추후 일정 랭킹이면 포인트 지급 구현 예정
	public int getMyScore(String memberId) {
		return quizLogDao.countCorrectAnswer(memberId);
	}
	
	// TOP랭킹 20위 가져오기
	public List<RankVO> getRankingList() {
	    return quizLogDao.selectRanking();
	}
	
	//어떤 사람이 해당 컨텐츠의 문제를 풀었는지 조회
	public List<QuizLogDto> quizLogList(long quizLogQuizId){
		return quizLogDao.selectList(quizLogQuizId);
	}
}





