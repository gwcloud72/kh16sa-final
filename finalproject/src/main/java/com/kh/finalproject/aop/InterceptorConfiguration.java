package com.kh.finalproject.aop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfiguration implements	WebMvcConfigurer {

	@Autowired
	private TokenRenewalInterceptor tokenRenewalInterceptor;
	@Autowired
	private MemberInterceptor memberInterceptor;

	public void addInterceptors(InterceptorRegistry registry) {
		
		//로그인 패싱 인터셉터
		registry.addInterceptor(memberInterceptor)
			.addPathPatterns("/member/logout")
			.excludePathPatterns("");
		
		//토큰 재발급 인터셉터
		registry.addInterceptor(tokenRenewalInterceptor)
			.addPathPatterns("/**")
			.excludePathPatterns(
					"member/join",
					"member/login",
					"member/logout",
					"member/refresh");
	}
}
