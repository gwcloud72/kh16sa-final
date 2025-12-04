package com.kh.finalproject.restcontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.finalproject.dto.contents.ContentsDetailDto;
import com.kh.finalproject.dto.contents.SearchResultDto;
import com.kh.finalproject.service.TmdbApiService;
import com.kh.finalproject.vo.contents.SaveRequestVO;

import lombok.RequiredArgsConstructor;

@CrossOrigin
@RestController
@RequestMapping("/api/tmdb")
@RequiredArgsConstructor
public class TmdbDataRestController {

		@Autowired
		private TmdbApiService tmdbApiService;
		
		/**
	     * 1. [검색 엔드포인트] 영화/TV 제목 검색 (TMDB API 호출)
	     * React 호출 예시: GET /api/tmdb/search?query=검색어
	     * @return SearchResultDto 목록 (클라이언트 목록 표시에 사용)
	     */
		@GetMapping("/search")
		public ResponseEntity<List<SearchResultDto>> searchContents(@RequestParam String query) {
			 // 검색어 유효성 검증
	        if (query == null || query.trim().isEmpty()) {
	            return ResponseEntity.badRequest().build(); // 400 Bad Request
	        }
	        
	        // Service의 검색 로직 호출
	        List<SearchResultDto> results = tmdbApiService.searchContents(query);
	        return ResponseEntity.ok(results); // 200 OK와 함께 JSON 데이터 반환
		}
		
		/**
	     * 2. [저장 엔드포인트] 클라이언트가 선택한 컨텐츠 ID를 상세 조회 후 DB에 저장
	     * React 호출 예시: POST /api/tmdb/save (JSON Body 포함)
	     * @return 저장된 컨텐츠의 상세 정보 (장르 이름 포함)
	     */
	    @PostMapping("/save")
	    public ResponseEntity<ContentsDetailDto> saveSelectedContent(@RequestBody SaveRequestVO requestVO) {
	        // 요청 데이터 유효성 검증
	        if (requestVO.getContentsId() == null || requestVO.getType() == null || 
	            (!requestVO.getType().equals("movie") && !requestVO.getType().equals("tv"))) {
	            return ResponseEntity.badRequest().build(); // 400 Bad Request
	        }
	        
	        try {
	            // Service의 저장 로직 호출 (상세 조회 -> DB 저장 -> 최종 DTO 반환)
	            ContentsDetailDto savedContent = tmdbApiService.saveContentByTmdbId(
	                requestVO.getContentsId(),
	                requestVO.getType()
	            );
	            // 저장 성공 시 200 OK와 함께 저장된 객체 (JSON) 반환
	            return ResponseEntity.ok(savedContent);
	            
	        } catch (RuntimeException e) {
	            System.err.println("컨텐츠 저장 실패: " + e.getMessage());
	            // 500 Internal Server Error 반환
	            return ResponseEntity.internalServerError().build();
	        }
	    }
	    
	    /*
	     * DB에 저장된 특정 컨텐츠의 상세 정보와 장르 리스트를 조회합니다.
	     * 예시: http://localhost:8080/api/tmdb/content/detail/11
	     */
	    @GetMapping("/content/detail/{contentsId}")
	    public ContentsDetailDto getContentDetail(@PathVariable Long contentsId) {
	        return tmdbApiService.getContentDetailWithGenres(contentsId);
	    }
}
