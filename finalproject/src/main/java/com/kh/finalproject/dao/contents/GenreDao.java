package com.kh.finalproject.dao.contents;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.finalproject.dto.contents.ContentsGenreDto;
import com.kh.finalproject.vo.contents.ContentsGenreMapVO;

@Repository
public class GenreDao {
	
	@Autowired
	private SqlSession sqlSession;
	
	
	 //장르 매핑 저장 (CONTENTS_GENRE_MAP)
    public void upsertGenreMap(ContentsGenreMapVO vo) {
    	sqlSession.insert("contents.upsertGenreMap", vo);
    }
    
    //장르 매핑 삭제 (업데이트 전용)
    public void deleteGenreMapping(Long contentsId) {
    	sqlSession.delete("contents.deleteGenreMap", contentsId);
    }

    //장르 마스터 저장 (CONTENTS_GENRE)
    public void upsertGenre(ContentsGenreDto dto) {
    	sqlSession.insert("contents.upsertGenre", dto);
    }
}
