package com.kh.finalproject.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.finalproject.dao.InventoryDao;
import com.kh.finalproject.dao.MemberDao;
import com.kh.finalproject.dao.MemberIconDao;
import com.kh.finalproject.dao.PointHistoryDao;
import com.kh.finalproject.dao.PointItemStoreDao;
import com.kh.finalproject.dao.PointWishlistDao;
import com.kh.finalproject.dto.InventoryDto;
import com.kh.finalproject.dto.MemberDto;
import com.kh.finalproject.dto.PointHistoryDto;
import com.kh.finalproject.dto.PointItemStoreDto;
import com.kh.finalproject.dto.PointWishlistDto;
import com.kh.finalproject.vo.MemberPointVO;
import com.kh.finalproject.vo.PointHistoryPageVO;
import com.kh.finalproject.vo.PointItemWishVO;

@Service
public class PointService {

    @Autowired private PointItemStoreDao pointItemDao;
    @Autowired private MemberDao memberDao;
    @Autowired private InventoryDao inventoryDao; 
    @Autowired private PointHistoryDao pointHistoryDao;
    @Autowired private PointWishlistDao pointWishlistDao;
    @Autowired private MemberIconDao memberIconDao;

    // 등급 점수 변환
    private int getLevelWeight(String level) {
        if (level == null) return 0;
        switch (level) {
            case "관리자": return 99;
            case "우수회원": return 2;
            case "일반회원": return 1;
            default: return 0;
        }
    }

    // [통합 결제 로직]
    private void processTransaction(String senderId, String receiverId, long itemNo, String trxType) {

        PointItemStoreDto item = pointItemDao.selectOneNumber(itemNo);
        if (item == null) throw new RuntimeException("상품 없음");
        if (item.getPointItemStock() <= 0) throw new RuntimeException("품절");

        // 1회 한정 아이템
        if (item.getPointItemIsLimitedPurchase() == 1) {
            boolean alreadyHas = inventoryDao
                .selectListByMemberId(receiverId)
                .stream()
                .anyMatch(i -> i.getInventoryItemNo() == itemNo);

            if (alreadyHas)
                throw new RuntimeException("이미 보유한 아이템입니다.");
        }

        // 등급 체크
        MemberDto sender = memberDao.selectOne(senderId);
        int userScore = getLevelWeight(sender.getMemberLevel());
        int reqScore = getLevelWeight(item.getPointItemReqLevel());

        if (!"관리자".equals(sender.getMemberLevel()) && userScore < reqScore)
            throw new RuntimeException("등급 부족");

        // 포인트 차감
        addPoint(senderId, -(int)item.getPointItemPrice(), trxType);

        // 재고 차감
        item.setPointItemStock(item.getPointItemStock() - 1);
        pointItemDao.update(item);

        // 인벤토리 지급
        InventoryDto inven = inventoryDao
            .selectListByMemberId(receiverId)
            .stream()
            .filter(i -> i.getInventoryItemNo() == itemNo)
            .findFirst()
            .orElse(null);

        if (inven != null) {
            inven.setInventoryQuantity(inven.getInventoryQuantity() + 1);
            inventoryDao.update(inven);
        } else {
            insertNewInventory(receiverId, itemNo);
        }
    }

    // 인벤토리 신규 등록 헬퍼
    private void insertNewInventory(String memberId, long itemNo) {
        InventoryDto inven = new InventoryDto();
        inven.setInventoryMemberId(memberId);
        inven.setInventoryItemNo(itemNo); 
        inven.setInventoryQuantity(1);
        inven.setInventoryEquipped("N");
        inventoryDao.insert(inven);
    }

    // [구매]
    @Transactional
    public void purchaseItem(String loginId, long itemNo) {
        // ★ [수정] "BUY" -> "USE" (DB 제약조건 준수)
        processTransaction(loginId, loginId, itemNo, "USE");
    }

    // [선물]
    @Transactional
    public void giftItem(String senderId, String targetId, long itemNo) {
        if (senderId.equals(targetId)) throw new RuntimeException("본인 선물 불가");
        if (memberDao.selectOne(targetId) == null) throw new RuntimeException("회원 없음");

        processTransaction(senderId, targetId, itemNo, "SEND");
    }

    // [취소/환불]
    @Transactional
    public void cancelItem(String loginId, long inventoryNo) {
        InventoryDto inven = inventoryDao.selectOne(inventoryNo);

        if (inven == null) throw new RuntimeException("아이템 없음");
        if (!loginId.equals(inven.getInventoryMemberId())) throw new RuntimeException("권한 없음");
        if ("Y".equals(inven.getInventoryEquipped()))
            throw new RuntimeException("사용 중인 아이템은 환불 불가");

        PointItemStoreDto item = pointItemDao.selectOneNumber(inven.getInventoryItemNo());

        // ★ [수정] "REFUND" -> "GET" (DB 제약조건 준수: 환불받는 건 '획득'으로 처리)
        addPoint(loginId, (int)item.getPointItemPrice(), "GET");

        // 재고 복구
        item.setPointItemStock(item.getPointItemStock() + 1);
        pointItemDao.update(item);

        decreaseInventoryOrDelete(inven);
    }
    
    // [관리자] 상품 등록/수정/삭제
    @Transactional
    public void addItem(String loginId, PointItemStoreDto itemDto) {
        MemberDto member = memberDao.selectOne(loginId);
        if (member == null || !member.getMemberLevel().equals("관리자")) throw new RuntimeException("관리자 권한 없음");
        pointItemDao.insert(itemDto);
    }

    @Transactional
    public void editItem(String loginId, PointItemStoreDto itemDto) {
        MemberDto member = memberDao.selectOne(loginId);
        if (member == null || !member.getMemberLevel().equals("관리자")) throw new RuntimeException("권한 없음");
        pointItemDao.update(itemDto); 
    }

    @Transactional
    public void deleteItem(String loginId, long itemNo) {
        MemberDto member = memberDao.selectOne(loginId);
        if (member == null || !member.getMemberLevel().equals("관리자")) throw new RuntimeException("권한 없음");
        pointItemDao.delete(itemNo);
    }

    // 인벤토리 아이템 폐기
    @Transactional
    public void discardItem(String loginId, long inventoryNo) {
        InventoryDto inven = inventoryDao.selectOne(inventoryNo);
        if (inven == null) throw new RuntimeException("아이템 없음");
        if (!inven.getInventoryMemberId().equals(loginId)) throw new RuntimeException("권한 없음");

        inventoryDao.delete(inventoryNo);
    }

    // [아이템 사용]
    @Transactional
    public void useItem(String loginId, long inventoryNo, String extraValue) {

        InventoryDto inven = inventoryDao.selectOne(inventoryNo);
        if (inven == null || !loginId.equals(inven.getInventoryMemberId())) {
            throw new RuntimeException("아이템 정보 없음 / 권한 없음");
        }

        PointItemStoreDto item = pointItemDao.selectOneNumber(inven.getInventoryItemNo());
        if (item == null) {
            throw new RuntimeException("존재하지 않는 아이템입니다.");
        }

        String type = item.getPointItemType();

        switch (type) {
            case "CHANGE_NICK":
                if (extraValue == null || extraValue.isBlank())
                    throw new RuntimeException("변경할 닉네임을 입력하세요.");

                if (memberDao.selectOneByMemberNickname(extraValue) != null)
                    throw new RuntimeException("이미 사용 중인 닉네임입니다.");

                MemberDto dto = new MemberDto();
                dto.setMemberId(loginId);
                dto.setMemberNickname(extraValue);

                if (!memberDao.updateNickname(dto))
                    throw new RuntimeException("닉네임 변경 실패");

                decreaseInventoryOrDelete(inven);
                break;

            case "VOUCHER":
                addPoint(loginId, (int) item.getPointItemPrice(), "GET");
                decreaseInventoryOrDelete(inven);
                break;

            case "RANDOM_POINT":
                int min = 100, max = 2000;
                int random = (int)(Math.random() * (max - min + 1)) + min;

                addPoint(loginId, random, "GET");
                decreaseInventoryOrDelete(inven);
                break;

            case "RANDOM_ICON":
            case "RANDOM_ROULETTE":
                throw new RuntimeException("해당 아이템은 전용 API를 이용하세요.");

            case "DECO_NICK":
            case "DECO_BG":
            case "DECO_ICON":

                if ("Y".equals(inven.getInventoryEquipped())) {
                    throw new RuntimeException("이미 착용 중인 아이템입니다.");
                }
                unequipByType(loginId, type);

                inven.setInventoryEquipped("Y");
                inventoryDao.update(inven);
                break;

            default:
                throw new RuntimeException("사용할 수 없는 아이템 타입입니다.");
        }
    }

    private void unequipByType(String memberId, String type) {
        List<InventoryDto> list = inventoryDao.selectListByMemberId(memberId);
        
        for (InventoryDto dto : list) {
            if (type.equals(dto.getPointItemType()) && "Y".equals(dto.getInventoryEquipped())) {
                dto.setInventoryEquipped("N");
                inventoryDao.update(dto);
            }
        }
    }
   
    private void decreaseInventoryOrDelete(InventoryDto inven) {
        if (inven.getInventoryQuantity() > 1) {
            inven.setInventoryQuantity(inven.getInventoryQuantity() - 1);
            inventoryDao.update(inven);
        } else {
            inventoryDao.delete(inven.getInventoryNo());
        }
    }

    // 찜 관련 로직
    @Transactional
    public boolean toggleWish(String loginId, long itemNo) {
        PointItemWishVO vo = PointItemWishVO.builder().memberId(loginId).itemNo(itemNo).build();
        int count = pointWishlistDao.checkWish(vo); 
        if (count > 0) {
            pointWishlistDao.delete(vo);
            return false;
        } else {
            pointWishlistDao.insert(vo);
            return true;
        }
    }
    
    public List<Long> getMyWishItemNos(String loginId) { 
        return pointWishlistDao.selectMyWishItemNos(loginId); 
    }
    
    public List<PointWishlistDto> getMyWishlist(String loginId) { 
        return pointWishlistDao.selectMyWishlist(loginId); 
    }
    
    @Transactional
    public void deleteWish(String loginId, long itemNo) {
        PointItemWishVO vo = PointItemWishVO.builder().memberId(loginId).itemNo(itemNo).build();
        pointWishlistDao.delete(vo);
    }
    
    // 출석체크/후원/포인트지급
    @Transactional
    public void addAttendancePoint(String loginId, int amount, String memo) {
        addPoint(loginId, amount, "GET");
    }

    @Transactional
    public void donatePoints(String loginId, String targetId, int amount) {

        // 1. 잔액 체크
        if (memberDao.selectOne(loginId).getMemberPoint() < amount) {
            throw new RuntimeException("잔액 부족");
        }

        // 2. 보내는 사람 차감 (여기서 실패하면 바로 에러)
        boolean sendResult = addPoint(loginId, -amount, "SEND");
        if (!sendResult) {
            throw new RuntimeException("보내는 사람 포인트 차감 실패 (사용자 정보 확인 필요)");
        }

        // 3. 받는 사람 지급 (여기가 핵심! 실패 시 에러를 던져야 롤백됨)
        boolean receiveResult = addPoint(targetId, amount, "RECEIVED");
        if (!receiveResult) {
            // ★ 여기서 에러를 던져야 1번 과정(보낸 사람 차감)도 같이 취소됩니다.
            throw new RuntimeException("받는 사람 포인트 지급 실패 (아이디가 올바르지 않습니다)");
        }
    }
    // 룰렛
    @Transactional
    public int playRoulette(String loginId) {
        List<InventoryDto> myItems = inventoryDao.selectListByMemberId(loginId);
        InventoryDto ticket = myItems.stream()
                .filter(i -> "RANDOM_ROULETTE".equals(i.getPointItemType())) 
                .findFirst()
                .orElseThrow(() -> new RuntimeException("룰렛 이용권이 없습니다."));

        int targetIndex = 0; 
        double random = Math.random() * 100;
        
        if (random < 5) targetIndex = 4;
        else if (random < 15) targetIndex = 0;
        else if (random < 25) targetIndex = 3;
        else if (random < 55) targetIndex = 2;
        else targetIndex = (Math.random() < 0.5) ? 1 : 5;

        int rewardPoint = 0;
        boolean consumeTicket = true;

        switch (targetIndex) {
            case 0: rewardPoint = 1000; break;
            case 2: rewardPoint = 500; break;
            case 4: rewardPoint = 2000; break;
            case 3: consumeTicket = false; break; 
            default: break; 
        }

        if (consumeTicket) {
            decreaseInventoryOrDelete(ticket);
        }

        if (rewardPoint > 0) {
            // ★ [수정] 룰렛 결과 지급도 "GET"으로 통일
            addPoint(loginId, rewardPoint, "GET");
        }

        return targetIndex;
    }

    public String getUserNickStyle(String loginId) {
        // 1. 내 아이템 조회 (위에서 수정한 Mapper가 실행됨)
        List<InventoryDto> myItems = inventoryDao.selectListByMemberId(loginId);
        
        // 2. 장착된 DECO_NICK 아이템 찾기
        InventoryDto equippedItem = myItems.stream()
            .filter(i -> "Y".equals(i.getInventoryEquipped()) 
                      && "DECO_NICK".equals(i.getPointItemType()))
            .findFirst()
            .orElse(null);

        // 3. 없으면 빈값 리턴
        if (equippedItem == null) return "";

        // 4. [수정] 아이템 이름으로 검사할 필요 없이, Content 값을 바로 리턴!
        // (DB에 'nick-rainbow'라고 저장되어 있다면 그게 그대로 나옴)
        String style = equippedItem.getPointItemContent();
        
        return style != null ? style : "";
    }
 // [수정] 내 포인트 및 치장 정보 조회
    public MemberPointVO getMyPointInfo(String loginId) {
        // 1. 회원 정보 조회
        MemberDto member = memberDao.selectMap(loginId);
        if (member == null) return null;

        // 2. 장착 중인 아이콘 조회 (Dao가 null을 리턴하면 기본 이미지로 대체)
        String equippedIconSrc = memberIconDao.selectEquippedIconSrc(loginId);
        if (equippedIconSrc == null) {
            // 기본 이미지 경로 (프로젝트 상황에 맞춰 수정하세요)
            equippedIconSrc = "https://i.postimg.cc/Wb3VBy9v/null.png"; 
        }

        // 3. 닉네임 스타일 조회 (helper 메서드 사용)
        String nickStyle = getUserNickStyle(loginId);

        // 4. 관리자는 무조건 관리자 스타일 적용
        if ("관리자".equals(member.getMemberLevel())) {
            nickStyle = "nick-admin";
        }

        // 5. VO 빌드 및 반환
        return MemberPointVO.builder()
                .memberId(member.getMemberId())
                .nickname(member.getMemberNickname())
                .point(member.getMemberPoint())
                .level(member.getMemberLevel())
                .nickStyle(nickStyle)
                .iconSrc(equippedIconSrc)
                .build();
    }
    public PointHistoryPageVO getHistoryList(String loginId, int page, String type) {
        int size = 10;
        int totalCount = pointHistoryDao.countHistory(loginId, type);
        int totalPage = (totalCount + size - 1) / size;
        
        int startRow = (page - 1) * size + 1;
        int endRow = page * size;
    
        List<PointHistoryDto> list = pointHistoryDao.selectListByMemberIdPaging(loginId, startRow, endRow, type);
        
        return PointHistoryPageVO.builder()
                .list(list)
                .totalPage(totalPage)
                .currentPage(page)
                .totalCount(totalCount)
                .build();
    }

    /**
     * 포인트 증감 전용 메서드
     * amount > 0 : 지급
     * amount < 0 : 차감
     */
    @Transactional
    public boolean addPoint(String memberId, int amount, String trxType) {

        MemberDto dto = new MemberDto();
        dto.setMemberId(memberId);
        dto.setMemberPoint(amount); 

        boolean result = memberDao.upPoint(dto);

        if (result) {
            PointHistoryDto history = PointHistoryDto.builder()
                .pointHistoryMemberId(memberId)
                .pointHistoryAmount(amount)
                .pointHistoryTrxType(trxType)
                .build();
            pointHistoryDao.insert(history);
        }
        return result;
    }
}