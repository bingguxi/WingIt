package kopo.poly.persistance.mapper;

import kopo.poly.dto.UserInfoDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface IAdminMapper {

    // 회원정보 리스트 조회하기
    List<UserInfoDTO> getUserList() throws Exception;

    // 회원정보 상세보기 (similar with 마이페이지 프로필 상세보기)
    UserInfoDTO getUserInfo(UserInfoDTO pDTO) throws Exception;

    // 회원정보 수정하기 (similar with 마이페이지 프로필 수정하기)
    void updateUserInfo(UserInfoDTO pDTO) throws Exception;

    // 회원정보 삭제하기 (similar with 마이페이지 프로필 탈퇴하기)
    void deleteUserInfo(UserInfoDTO pDTO) throws Exception;

}
