package kopo.poly.persistance.mapper;

import kopo.poly.dto.UserInfoDTO;
import org.apache.catalina.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ILoginMapper {

    // 로그인 시 아이디, 비밀번호 일치하는지 확인하기
    UserInfoDTO getLogin(UserInfoDTO pDTO) throws Exception;

    // 네이버 로그인 시 아이디만 받아서 DB 조회하기
    UserInfoDTO getUserInfoById(UserInfoDTO pDTO) throws Exception;

    // 이름, 이메일 입력값 비교해서 아이디 값 반환
    UserInfoDTO findId(UserInfoDTO pDTO) throws Exception;

    // 아이디, 이름, 이메일 입력값 비교해서 existsYn 값 반환
    UserInfoDTO findPwd(UserInfoDTO pDTO) throws Exception;

    // 생성된 임시 비밀번호로 비밀번호 재설정
    void updatePassword(UserInfoDTO pDTO) throws Exception;

    // 로그인 되어 있는 유저의 identity 조회하기
    UserInfoDTO getIdentity(UserInfoDTO pDTO) throws Exception;

}