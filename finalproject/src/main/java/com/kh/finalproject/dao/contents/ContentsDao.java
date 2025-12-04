package com.kh.finalproject.dao.contents;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.finalproject.dto.contents.ContentsDetailDto;
import com.kh.finalproject.vo.contents.ContentsVO;

@Repository
public class ContentsDao {
	
	@Autowired
	private SqlSession sqlSession;
	
	//컨텐츠 저장/갱신 (contents 테이블)
    public void upsertContent(ContentsVO vo) {
    	sqlSession.insert("contents.upsertContent", vo);
    } 
    //컨텐츠 상세 조회 및 장르 이름 리스트 조회
    public ContentsDetailDto selectContentDetailWithGenres(Long contentsId) {
    	return sqlSession.selectOne("contents.selectContentDetailWithGenres", contentsId);
    }
}
