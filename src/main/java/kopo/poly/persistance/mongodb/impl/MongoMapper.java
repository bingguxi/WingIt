package kopo.poly.persistance.mongodb.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import kopo.poly.dto.ChatDTO;
import kopo.poly.persistance.mongodb.AbstractMongoDBComon;
import kopo.poly.persistance.mongodb.IMongoMapper;
import kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class MongoMapper extends AbstractMongoDBComon implements IMongoMapper {

    private final MongoTemplate mongodb;

    @Override
    public int insertData(ChatDTO pDTO, String colNm) throws Exception {

        log.info(this.getClass().getName() + ".insertData Start!");

        int res = 0;

        // 데이터를 저장할 컬렉션 생성
        super.createCollection(mongodb, colNm);

        // 저장할 컬렉션 객체 생성
        MongoCollection<Document> col = mongodb.getCollection(colNm);

        // DTO를 Map 데이터타입으로 변경한 뒤, 변경된 Map 데이터타입을 Document로 변경하기
        col.insertOne(new Document(new ObjectMapper().convertValue(pDTO, Map.class)));

        res = 1;

        log.info(this.getClass().getName() + ".insertData End!");

        return res;

    }

    @Override
    public List<ChatDTO> getChats(String colNm, ChatDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".getChats Start!");

        // 조회 결과를 전달하기 위한 객체 생성하기
        List<ChatDTO> rList = new LinkedList<>();

        MongoCollection<Document> col = mongodb.getCollection(colNm);

        // 조회할 조건 (SQL의 WHERE 역할)
        Document query = new Document();
        query.append("name", CmmUtil.nvl(pDTO.name()));

        // 조회 결과 중 출력할 컬럼들 (SQL의 SELECT절과 FROM절 가운데 컬럼들과 유사함)
        Document projection = new Document();
        projection.append("msg", "$msg");
        projection.append("sender", "$sender");
        projection.append("date", "$date");

        // MongoDB는 무조건 ObjectId가 자동생성되며, ObjectID는 사용하지 않을때, 조회할 필요가 없음
        // ObjectId를 가지고 오지 않을 때 사용함
        projection.append("_id", 0);

        Document sort = new Document();

        sort.append("date", 1);

        // MongoDB의 find 명령어를 통해 조회할 경우 사용함
        // 조회하는 데이터의 양이 적은 경우, find를 사용하고, 데이터양이 많은 경우 무조건 Aggregate 사용한다.
        FindIterable<Document> rs = col.find(query).projection(projection).sort(sort);

        for (Document doc : rs) {

            String msg = CmmUtil.nvl(doc.getString("msg"));
            String sender = CmmUtil.nvl(doc.getString("sender"));
            String date = CmmUtil.nvl(doc.getString("date"));

            ChatDTO rDTO = ChatDTO.builder().msg(msg).sender(sender).date(date).build();

            // 레코드 결과를 List에 저장하기
            rList.add(rDTO);

            rDTO = null;
            doc = null;

        }

        log.info(this.getClass().getName() + ".getChats End!");

        return rList;
    }

    @Override
    public List<ChatDTO> getChatList(String colNm) throws Exception {

        log.info(this.getClass().getName() + ".getChatList Start!");

        // 조회 결과를 전달하기 위한 객체 생성하기
        List<ChatDTO> rList = new LinkedList<>();

        // 중복 제거를 위한 Set
        Set<String> uniqueNames = new HashSet<>();

        MongoCollection<Document> col = mongodb.getCollection(colNm);

        // 조회할 조건 (SQL의 WHERE 역할)
        Document query = new Document();

        // 조회 결과 중 출력할 컬럼들 (SQL의 SELECT절과 FROM절 가운데 컬럼들과 유사함)
        Document projection = new Document();
        projection.append("name", "$name");

        // MongoDB는 무조건 ObjectId가 자동생성되며, ObjectID는 사용하지 않을때, 조회할 필요가 없음
        // ObjectId를 가지고 오지 않을 때 사용함
        projection.append("_id", 0);

        // MongoDB의 find 명령어를 통해 조회할 경우 사용함
        // 조회하는 데이터의 양이 적은 경우, find를 사용하고, 데이터양이 많은 경우 무조건 Aggregate 사용한다.
        FindIterable<Document> rs = col.find(query).projection(projection);

        for (Document doc : rs) {

            String name = CmmUtil.nvl(doc.getString("name"));

            if (uniqueNames.add(name)) { // Set에 추가될 때만 리스트에 추가
                ChatDTO rDTO = ChatDTO.builder().name(name).build();

                // 레코드 결과를 List에 저장하기
                rList.add(rDTO);
            }

            doc = null;

        }

        log.info(this.getClass().getName() + ".getChatList End!");

        return rList;
    }

    @Override
    public void deleteChatInfo(String colNm, ChatDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".deleteChatInfo Start!");

        MongoCollection<Document> col = mongodb.getCollection(colNm);

        String name = CmmUtil.nvl(pDTO.name());

        log.info("colNm : " + colNm);
        log.info("pDTO : " + pDTO);

        // 조회할 조건 (SQL의 WHERE 역할)
        Document query = new Document();
        query.append("name", name);

        // MongoDB 데이터 삭제는 반드시 컬렉션을 조회하고, 조회된 ObjectID를 기반으로 데이터를 삭제함
        // MongoDB 환경은 분산환경(Sharding)으로 구성될 수 있기 때문에 정확한 PK에 매핑하기 위해서임
        FindIterable<Document> rs = col.find(query);

        // 람다식 활용하여 데이터 삭제하기
        // 전체 컬렉션에 있는 데이터들을 삭제하기
        rs.forEach(col::deleteOne); // Col 객체에 자동으로 매칭되어 실행될 함수 정의
//        rs.forEach(doc -> col.deleteOne(doc)); // rs.forEach(col::deleteOne); 과 동일한 문법

        log.info(this.getClass().getName() + ".deleteChatInfo End!");

    }

}
