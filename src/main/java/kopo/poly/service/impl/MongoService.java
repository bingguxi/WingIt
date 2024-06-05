package kopo.poly.service.impl;

import kopo.poly.dto.ChatDTO;
import kopo.poly.persistance.mongodb.IMongoMapper;
import kopo.poly.service.IMongoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class MongoService implements IMongoService {

    private final IMongoMapper mongoMapper; // MongoDB에 저장할 Mapper

    @Override
    public void insertChat(ChatDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".insertChat Start!");

        // 생성할 컬렉션명
        String colNm = "CHAT";

        // MongoDB에 데이터 저장하기
        int res = mongoMapper.insertData(pDTO, colNm);

        log.info(this.getClass().getName() + ".insertChat End!");

    }

    @Override
    public List<ChatDTO> getChats(ChatDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".getChats Start!");

        // MongoDB에 저장된 컬렉션 이름
        String colNm = "CHAT";

        // 결과값
        List<ChatDTO> rList = mongoMapper.getChats(colNm, pDTO);

        log.info(this.getClass().getName() + ".getChats End!");

        return rList;
    }

    @Override
    public List<ChatDTO> getChatList() throws Exception {

        log.info(this.getClass().getName() + ".getChatList Start!");

        // MongoDB에 저장된 컬렉션 이름
        String colNm = "CHAT";

        // 결과값
        List<ChatDTO> rList = mongoMapper.getChatList(colNm);

        log.info(this.getClass().getName() + ".getChatList End!");

        return rList;
    }

    @Override
    public void deleteChatInfo(ChatDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".deleteChatInfo Start!");

        // MongoDB에 저장된 컬렉션 이름
        String colNm = "CHAT";

        // MongoDB에 존재하는 데이터 삭제하기
        mongoMapper.deleteChatInfo(colNm, pDTO);

        log.info(this.getClass().getName() + ".deleteChatInfo End!");

    }

}
