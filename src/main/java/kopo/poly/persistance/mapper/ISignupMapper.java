package kopo.poly.persistance.mapper;

import kopo.poly.dto.UserInfoDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ISignupMapper {

    // 아이디 중복 체크하기
    UserInfoDTO getUserIdExists(UserInfoDTO pDTO) throws Exception;

    // 닉네임 중복 체크하기
    UserInfoDTO getNicknameExists(UserInfoDTO pDTO) throws Exception;

    // 이메일 중복 체크하기
    UserInfoDTO getEmailExists(UserInfoDTO pDTO) throws Exception;

    // 회원 가입하기
    int insertUserInfo(UserInfoDTO pDTO) throws Exception;

}