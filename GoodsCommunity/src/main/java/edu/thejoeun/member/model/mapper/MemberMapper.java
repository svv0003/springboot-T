package edu.thejoeun.member.model.mapper;

import edu.thejoeun.member.model.dto.Member;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberMapper {

    Member getMemberByEmail(String memberEmail);

/*
    데이터 저장 후 저장된 멤버 데이터 내용 조회
    Member saveMember(Member member);

    0 과 1을 기준으로 데이터 저장 성공여부
    0과 1이 아닌 수가 나오면 데이터가 n개만큼 저장된 상태 확인
    주로 상품 여러개 등록, 게시물 여러개 등록
    int saveMember(Member member);
 */
    // 단순 저장으로 반환 없음
    void saveMember(Member member);
    void updateMember(Member member);
    void updateProfileImage(String memberEmail, String memberProfileImage);
}
