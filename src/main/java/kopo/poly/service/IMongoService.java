package kopo.poly.service;

import kopo.poly.dto.ChatDTO;

import java.util.List;

public interface IMongoService {

    /**
     * 간단한 데이터 저장하기
     */
    void insertChat(ChatDTO pDTO) throws Exception;

    /**
     * 세션 유저아이디로 채팅내역 조회하기
     *
     * @param pDTO 유저아이디
     * @return 채팅 내역
     */
    List<ChatDTO> getChats(ChatDTO pDTO) throws Exception;

    /**
     * 관리자 페이지에서 고객센터 이용한 유저 리스트 조회하기
     *
     * @return 고객센터 이용한 유저아이디 리스트
     */
    List<ChatDTO> getChatList() throws Exception;

    /**
     * 탈퇴하는 유저의 채팅 내역 삭제하기
     * 
     * @param pDTO 탈퇴하는 유저아이디
     */
    void deleteChatInfo(ChatDTO pDTO) throws Exception;

}
