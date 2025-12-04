package com.kh.finalproject.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.kh.finalproject.dto.contents.ContentsDetailDto;
import com.kh.finalproject.dto.contents.ContentsGenreDto;
import com.kh.finalproject.vo.contents.ContentsGenreMapVO;
import com.kh.finalproject.vo.contents.ContentsVO;

@Mapper
public interface ContentsMapper {
	
	 // 1. 컨텐츠 저장/갱신 (CONTENTS 테이블)
    void upsertContent(ContentsVO vo); 

    // 2. 장르 매핑 저장 (CONTENTS_GENRE_MAP)
    void upsertGenreMap(ContentsGenreMapVO vo);
    
    // 3. 장르 매핑 삭제 (업데이트 전용)
    void deleteGenreMapping(Long contentsId);

    // 4. 장르 마스터 저장 (CONTENTS_GENRE)
    void upsertGenre(ContentsGenreDto dto);
    
    // 5. 컨텐츠 상세 조회 및 장르 이름 리스트 조회
    ContentsDetailDto selectContentDetailWithGenres(Long contentsId);
}
