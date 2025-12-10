package com.kh.finalproject.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.finalproject.dto.WatchlistDto;

@Repository
public class WatchlistDao {

	@Autowired
	private SqlSession sqlSession;
	
	// 등록
	public void insert(WatchlistDto watchlistDto) {
		sqlSession.insert("watchlist.insert", watchlistDto);
	}
	
	//삭제
	public boolean delete(Long watchlistContent, String watchlistMember) {
		WatchlistDto watchlistDto = new WatchlistDto();
		watchlistDto.setWatchlistContent(watchlistContent);
		watchlistDto.setWatchlistMember(watchlistMember);
		return sqlSession.delete("watchlist.delete", watchlistDto) > 0;
	}
	
}
