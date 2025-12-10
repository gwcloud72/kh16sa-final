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

import com.kh.finalproject.dao.ReviewDao;
import com.kh.finalproject.dto.ReviewDto;
import com.kh.finalproject.error.TargetNotfoundException;


@CrossOrigin
@RestController
@RequestMapping("/review")
public class ReviewRestController {
	@Autowired
	private ReviewDao reviewDao;
	
	//등록
	@PostMapping("/")
	public void insert(@RequestBody ReviewDto reviewDto) {
		reviewDao.insert(reviewDto);
	}
	
	//전체 리뷰 조회
	@GetMapping("/reviewContents/{reviewContents}")
	public List<ReviewDto> selectByContents(@PathVariable Long reviewContents) {
	    return reviewDao.selectByContents(reviewContents);
	}
	
	//로그인 리뷰 조회
	@GetMapping("/user/{reviewContents}/{loginId}")
	public ReviewDto selectByUserAndContents(@PathVariable String loginId,
											@PathVariable Long reviewContents) {
		ReviewDto reviewDto = reviewDao.selectByUserAndContents(loginId, reviewContents);
		return reviewDto;
	}

//	//영화 제목으로 조회
//	@GetMapping("/{contentsTitle}")
//	public List<ReviewDto> selectByTitle(@PathVariable String contentsTitle) {
//		List<ReviewDto> reviewList = reviewDao.detail(contentsTitle);
//		if(reviewList == null || reviewList.isEmpty()) throw new TargetNotfoundException();
//		return reviewList;
//	}
	
	//수정
	@PatchMapping("/{reviewNo}")
	public void updateUnit(@RequestBody ReviewDto reviewDto,
								@PathVariable Long reviewNo) {
		ReviewDto originDto = reviewDao.selectOne(reviewNo);
		if(originDto == null) throw new TargetNotfoundException();
		
		reviewDto.setReviewNo(reviewNo);
		
		boolean success = reviewDao.updateUnit(reviewDto);
		if(!success) {
			throw new TargetNotfoundException(); //수정실패
		}
	}

	//삭제
	@DeleteMapping("/{reviewNo}")
	public void delete(@PathVariable Long reviewNo) {
		ReviewDto originDto = reviewDao.selectOne(reviewNo);
		if(originDto == null)throw new TargetNotfoundException();
		
		boolean success = reviewDao.delete(reviewNo);
		if(!success) {
			throw new TargetNotfoundException(); //삭제실패
		}
	}
	
	
	
	
	
	
	
	
	
	
	
}
