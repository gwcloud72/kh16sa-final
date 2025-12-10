package com.kh.finalproject.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.finalproject.dto.ReviewDto;

@Repository
public class ReviewDao {
	@Autowired
	private SqlSession sqlSession;
	
	//등록
	public void insert(ReviewDto reviewDto) {
		sqlSession.insert("review.insert", reviewDto);
	}
	
	//전체 조회
	public List<ReviewDto> selectByContents(Long reviewContents) { //전체
		return sqlSession.selectList("review.selectList", reviewContents);
	}
	
	//내리뷰 조회
//	public ReviewDto selectByUserAndContents(
//			@Param("loginId") String loginId,
//			@Param("reviewContents") Long reviewContents) {
//		return sqlSession.selectOne("review.selectByUserAndContents");
//	}
	public ReviewDto selectByUserAndContents(String loginId, Long reviewContents) {
	    Map<String, Object> map = new HashMap<>();
	    map.put("loginId", loginId);
	    map.put("reviewContents", reviewContents);
	    return sqlSession.selectOne("review.selectByUserAndContents", map);
	}
	
	
	public ReviewDto selectOne(Long reviewNo) {
		return sqlSession.selectOne("review.selectOne", reviewNo);
	}
	
//	public List<ReviewDto> detail(String contentsTitle) { //컨텐츠 제목으로 조회
//		return sqlSession.selectList("review.detail", contentsTitle);
//	}
	
	//부분수정
	public boolean updateUnit(ReviewDto reviewDto) {
		return sqlSession.update("review.updateUnit", reviewDto) > 0;
	}
	
	//삭제
	public boolean delete(Long reviewNo) {
		return sqlSession.delete("review.delete", reviewNo) > 0;
	}
}
