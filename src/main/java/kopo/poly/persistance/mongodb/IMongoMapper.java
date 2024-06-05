package kopo.poly.persistance.mongodb;

import kopo.poly.dto.ChatDTO;

import java.util.List;

public interface IMongoMapper {

    /**
     * 간단한 데이터 저장하기
     * 
     * @param pDTO 저장될 정보
     * @param colNm 저장할 컬렉션 이름
     * @return 저장 결과
     * @throws Exception
     */
    int insertData(ChatDTO pDTO, String colNm) throws Exception;

    /**
     * 세션 유저아이디로 채팅내역 조회하기
     *
     * @param colNm 조회할 컬렉션 이름
     * @param pDTO 유저아이디
     * @return 채팅내역
     */
    List<ChatDTO> getChats(String colNm, ChatDTO pDTO) throws Exception;

    /**
     * 관리자 페이지에서 고객센터 이용한 유저 리스트 조회하기
     *
     * @param colNm 조회할 컬렉션 이름
     * @return 고객센터 이용한 유저아이디 리스트
     */
    List<ChatDTO> getChatList(String colNm) throws Exception;

    /**
     * 탈퇴하는 유저의 채팅 내역 삭제하기
     * 
     * @param colNm 조회할 컬렉션 이름
     * @param pDTO 탈퇴하는 유저아이디
     */
    void deleteChatInfo(String colNm, ChatDTO pDTO) throws Exception;

}
