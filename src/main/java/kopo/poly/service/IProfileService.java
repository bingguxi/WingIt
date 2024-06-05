package kopo.poly.service;

import kopo.poly.dto.LikeJobDTO;
import kopo.poly.dto.TestResultDTO;
import kopo.poly.dto.UserInfoDTO;

import java.util.List;

public interface IProfileService {

    // 마이페이지 회원정보 조회
    UserInfoDTO getProfile(UserInfoDTO pDTO) throws Exception;

    // 마이페이지 회원정보 수정
    void updateProfile(UserInfoDTO pDTO) throws Exception;

    // 마이페이지 회원 탈퇴
    void deleteUserInfo(UserInfoDTO pDTO) throws Exception;

    // 마이페이지 진로심리검사 내역 조회하기
    List<TestResultDTO> getTestRecordList(TestResultDTO pDTO) throws Exception;

    // 마이페이지 진로심리검사 내역 삭제하기
    void deleteTestRecord(TestResultDTO pDTO) throws Exception;

    // 좋아요 한 직업 모두 조회하기
    List<LikeJobDTO> getLikeJobList(LikeJobDTO pDTO) throws Exception;

}
