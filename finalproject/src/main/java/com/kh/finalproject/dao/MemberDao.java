package com.kh.finalproject.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import com.kh.finalproject.dto.MemberDto;

@Repository
public class MemberDao {

	@Autowired
	private SqlSession sqlSession;
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	/// 등록
	public void insert(MemberDto memberDto) {
		//++ 비밀번호 암호화
		String origin = memberDto.getMemberPw();
		String encoded = passwordEncoder.encode(origin); // 암호화
		memberDto.setMemberPw(encoded);
		sqlSession.insert("member.insert", memberDto);
	}
	
	/// 조회
		// 기본조회(목록)
		public List<MemberDto> selectList() {
			return sqlSession.selectList("member.selectList");
		}
		// 상세조회
		public MemberDto selectOne(String memberId) {
			return sqlSession.selectOne("member.detail", memberId);
		}
		// 닉네임 중복 검사용 조회
		public MemberDto selectOneByMemberNickname(String memberNickname) {
			return sqlSession.selectOne("member.detailByNickname", memberNickname);
		}
	
	/// 수정
		//(회원기본정보 수정)
		public boolean update(MemberDto memberDto) {
			return sqlSession.update("member.update", memberDto) > 0 ;
		}
		//(닉네임 수정)
		// + 컨트롤러에서 닉네임 수정할때 포인트 차감이 필요할지?
		// + 포인트가 부족하면 닉네임 수정이 불가능할지
		public boolean updateNickname(MemberDto memberDto) {
			return sqlSession.update("member.updateNickname", memberDto) > 0 ;
		}
		//(포인트 갱신)
		public boolean updatePoint(MemberDto memberDto) {
			return sqlSession.update("member.updatePoint", memberDto) > 0 ;
		}
		//(신뢰도 갱신)
		public boolean updateReliability(MemberDto memberDto) {
			return sqlSession.update("member.updateReliability", memberDto) > 0 ;
		}

	/// 삭제 (회원탈퇴)
	public boolean delete(String memberId) {
		return sqlSession.delete("member.delete",memberId) > 0;
	}


	
	
	
}
