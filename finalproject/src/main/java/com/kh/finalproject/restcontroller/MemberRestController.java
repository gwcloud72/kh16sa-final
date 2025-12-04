package com.kh.finalproject.restcontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.finalproject.dao.MemberDao;
import com.kh.finalproject.dto.MemberDto;
import com.kh.finalproject.error.TargetNotfoundException;

@CrossOrigin
@RestController
@RequestMapping("/member")
public class MemberRestController {

	@Autowired
	private MemberDao memberDao;
	
	//회원가입
	@PostMapping("/")
	public void join(@RequestBody MemberDto memberDto) {
		memberDao.insert(memberDto);
	}
	
	//회원 조회
	@GetMapping("/")
	public List<MemberDto> selectList(){
		return memberDao.selectList();
	}
	
	//회원정보수정 (전체수정)
	@PutMapping("/{memberId}")
	public void edit(
			@PathVariable String memberId,
			@RequestBody MemberDto memberDto) {
		MemberDto originDto = memberDao.selectOne(memberId);
		if(originDto == null) throw new TargetNotfoundException();
		// 각 요소 입력
		originDto.setMemberPw(memberDto.getMemberPw());
		originDto.setMemberEmail(memberDto.getMemberEmail());
		originDto.setMemberBirth(memberDto.getMemberBirth());
		originDto.setMemberContact(memberDto.getMemberContact());
		originDto.setMemberLevel(memberDto.getMemberLevel());
		originDto.setMemberPost(memberDto.getMemberPost());
		originDto.setMemberAddress1(memberDto.getMemberAddress1());
		originDto.setMemberAddress2(memberDto.getMemberAddress2());
		memberDao.update(originDto);
	}
	//포인트 갱신
	
	//신뢰도 갱신
	
	//로그인
	
	//로그아웃
	
	//회원탈퇴
	@DeleteMapping("/{memberId}")
	public void delete(@PathVariable String memberId) {
		MemberDto memberDto = memberDao.selectOne(memberId);
		if(memberDto ==null) throw new TargetNotfoundException("존재하지 않는 회원입니다");
		memberDao.delete(memberId);
	}
}
