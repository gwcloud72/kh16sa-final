package com.kh.finalproject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.finalproject.configuration.JwtProperties;
import com.kh.finalproject.dao.MemberTokenDao;

@Service
public class TokenService {

	@Autowired
	private JwtProperties  jwtProperties;
	@Autowired
	private MemberTokenDao memberTokenDao;
	
	// AccessToken 생성
	
	
	// RefreshToken 생성
	
	
	// Parse
	
	
	// 토큰 만료까지 남은시간을 구하는 기능
	
	
	// checkRefresh Token
	
	
}
