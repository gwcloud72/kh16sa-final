package com.kh.finalproject.service;

import com.kh.finalproject.configuration.DailyQuestProperties;
import com.kh.finalproject.dao.MemberDao;
import com.kh.finalproject.dao.PointGetQuestDao;
import com.kh.finalproject.dto.QuestResponseDto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestService {

    private final PointGetQuestDao questDao;        
    private final DailyQuestProperties questProperties; // 
    private final MemberDao memberDao;              // ★ 포인트 지급을 위해 필요하다면 추가하세요

    /**
     * 1. 일일 퀘스트 목록 조회
     * (설정 파일의 퀘스트 목록 + DB의 내 진행 상황 합치기)
     */
    public List<QuestResponseDto> getQuestList(String userId) {
        // 오늘 날짜 구하기 (YYYYMMDD)
        String today = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);

        // 1. DB에서 나의 오늘 퀘스트 기록 가져오기
        List<Map<String, Object>> logs = questDao.selectTodayLogs(userId, today);

        // 2. 검색하기 쉽게 Map<String(타입), Map(로그데이터)> 형태로 변환
        Map<String, Map<String, Object>> logMap = logs.stream()
                .collect(Collectors.toMap(
                        m -> (String) m.get("type"), // key: 퀘스트 타입 (REVIEW, QUIZ...)
                        m -> m                       // value: 로그 전체
                ));

        List<QuestResponseDto> result = new ArrayList<>();

        // 3. 설정 파일(properties)에 정의된 퀘스트 목록을 기준으로 루프 돌기
        for (DailyQuestProperties.QuestDetail config : questProperties.getList()) {
            
            // 내 기록이 있는지 확인
            Map<String, Object> log = logMap.get(config.getType());

            // 값 계산 (DB에서 가져온 값이 없으면 0)
            int currentCount = 0;
            boolean isClaimed = false;

            if (log != null) {
                // Oracle에서 숫자는 BigDecimal로 올 수 있으므로 안전하게 변환
                Object countObj = log.get("currentCount");
                currentCount = (countObj instanceof BigDecimal) ? ((BigDecimal) countObj).intValue() : (Integer) countObj;
                
                // 보상 수령 여부 (Y/N)
                isClaimed = "Y".equals(log.get("rewardYn"));
            }

            // 목표 달성 여부 확인
            boolean isDone = currentCount >= config.getTarget();

            // DTO 생성 및 추가
            result.add(QuestResponseDto.builder()
                    .type(config.getType())      // "REVIEW"
                    .title(config.getTitle())    // "Write a Review"
                    .targetCount(config.getTarget())
                    .reward(config.getReward())
                    .currentCount(currentCount)
                    .isDone(isDone)
                    .isClaimed(isClaimed)
                    .build());
        }

        return result;
    }

    /**
     * 2. 퀘스트 진행도 증가
     * (유저가 행동을 했을 때 호출)
     */
    public void increaseProgress(String userId, String type) {
        String today = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        
        // DAO 호출 (없으면 생성, 있으면 +1)
        questDao.upsertQuestLog(userId, type, today);
    }

    /**
     * 3. 보상 받기
     * (보상 수령 상태 변경 + 실제 포인트 지급)
     */
    @Transactional
    public int claimReward(String userId, String type) {
        String today = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);

        // 1. 설정 파일에서 해당 퀘스트의 보상 포인트 찾기
        int rewardPoint = questProperties.getList().stream()
                .filter(q -> q.getType().equals(type)) // "REVIEW"랑 같은거 찾기
                .findFirst()
                .map(DailyQuestProperties.QuestDetail::getReward)
                .orElse(0); // 없으면 0

        if (rewardPoint == 0) {
            throw new IllegalArgumentException("존재하지 않는 퀘스트 타입입니다.");
        }

        // 2. DB 업데이트: 보상 수령 상태 'Y'로 변경
        // 이미 받았는지 체크하는 로직은 SQL이나 여기서 추가 가능
        int updatedRows = questDao.updateRewardStatus(userId, type, today);
        
        if (updatedRows == 0) {
            throw new IllegalStateException("아직 퀘스트를 완료하지 않았거나 이미 보상을 받았습니다.");
        }

        // 3. ★ 중요: 실제 유저 테이블(MEMBER)에 포인트 지급 로직 추가 ★
        // memberDao.updatePoint(userId, rewardPoint); 
        // System.out.println(userId + "님에게 " + rewardPoint + " 포인트 지급 완료");

        return rewardPoint; // 받은 포인트 리턴 (프론트 알림용)
    }
    
}