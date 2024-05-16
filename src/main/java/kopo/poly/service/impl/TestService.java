package kopo.poly.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import kopo.poly.dto.TestDTO;
import kopo.poly.dto.UserInfoDTO;
import kopo.poly.persistance.mapper.ITestMapper;
import kopo.poly.service.ITestService;
import kopo.poly.util.NetworkUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class TestService implements ITestService {

    private final ITestMapper testMapper;

    @Value("${career-net.api.key}")
    private String apiKey;

    @Transactional
    @Override
    public void insertHMstudent() throws Exception {

        log.info(this.getClass().getName() + ".insertHMstudent Start!");

        String apiParam = "https://www.career.go.kr/inspct/openapi/test/questions?apikey=" + apiKey + "&q=31";

        log.info("apiParam : " + apiParam);

        Map<String, String> headers = new HashMap<>();

        String json = NetworkUtil.get(apiParam, headers);

        Map<String, Object> rMap = new ObjectMapper().readValue(json, LinkedHashMap.class);

        // log.info("rMap : " + rMap); // rMap : {SUCC_YN=Y, ERROR_REASON=, RESULT=[{question=생명체의 기원, 발달 및 원리 등을 연구한다., answer01=매우 싫다, answer02=약간 싫다, answer03

        List<Map<String, Object>> dataList = (List<Map<String, Object>>) rMap.get("RESULT");

        for (Map<String, Object> dataMap : dataList) {
            TestDTO pDTO = TestDTO.builder()
                    .question(String.valueOf(dataMap.get("question")))
                    .answer01(String.valueOf(dataMap.get("answer01")))
                    .answer02(String.valueOf(dataMap.get("answer02")))
                    .answer03(String.valueOf(dataMap.get("answer03")))
                    .answer04(String.valueOf(dataMap.get("answer04")))
                    .build();

            // log.info("pDTO.toString() : " + pDTO.toString());

            testMapper.insertHMstudent(pDTO);

        }

        log.info(this.getClass().getName() + ".insertHMstudent End!");

    }

    @Transactional
    @Override
    public void insertGCGstudent() throws Exception {

        log.info(this.getClass().getName() + ".insertGCGstudent Start!");

        String apiParam = "https://www.career.go.kr/inspct/openapi/test/questions?apikey=" + apiKey + "&q=25";

        log.info("apiParam : " + apiParam);

        Map<String, String> headers = new HashMap<>();

        String json = NetworkUtil.get(apiParam, headers);

        Map<String, Object> rMap = new ObjectMapper().readValue(json, LinkedHashMap.class);

        log.info("rMap : " + rMap); // rMap : {SUCC_YN=Y, ERROR_REASON=, RESULT=[{question=생명체의 기원, 발달 및 원리 등을 연구한다., answer01=매우 싫다, answer02=약간 싫다, answer03

        List<Map<String, Object>> dataList = (List<Map<String, Object>>) rMap.get("RESULT");

        for (Map<String, Object> dataMap : dataList) {
            TestDTO pDTO = TestDTO.builder()
                    .question(String.valueOf(dataMap.get("question")))
                    .answer01(String.valueOf(dataMap.get("answer01")))
                    .answer02(String.valueOf(dataMap.get("answer02")))
                    .answer03(String.valueOf(dataMap.get("answer03")))
                    .answer04(String.valueOf(dataMap.get("answer04")))
                    .answer05(String.valueOf(dataMap.get("answer05")))
                    .build();

            log.info("pDTO.toString() : " + pDTO.toString());

            testMapper.insertGCGstudent(pDTO);

        }

        log.info(this.getClass().getName() + ".insertGCGstudent End!");

    }

    @Transactional
    @Override
    public void insertGCGadult() throws Exception {

        log.info(this.getClass().getName() + ".insertGCGadult Start!");

        String apiParam = "https://www.career.go.kr/inspct/openapi/test/questions?apikey=" + apiKey + "&q=6";

        log.info("apiParam : " + apiParam);

        Map<String, String> headers = new HashMap<>();

        String json = NetworkUtil.get(apiParam, headers);

        Map<String, Object> rMap = new ObjectMapper().readValue(json, LinkedHashMap.class);

        log.info("rMap : " + rMap); // rMap : {SUCC_YN=Y, ERROR_REASON=, RESULT=[{question=생명체의 기원, 발달 및 원리 등을 연구한다., answer01=매우 싫다, answer02=약간 싫다, answer03

        List<Map<String, Object>> dataList = (List<Map<String, Object>>) rMap.get("RESULT");

        for (Map<String, Object> dataMap : dataList) {
            TestDTO pDTO = TestDTO.builder()
                    .question(String.valueOf(dataMap.get("question")))
                    .answer01(String.valueOf(dataMap.get("answer01")))
                    .answer02(String.valueOf(dataMap.get("answer02")))
                    .answer03(String.valueOf(dataMap.get("answer03")))
                    .answer04(String.valueOf(dataMap.get("answer04")))
                    .build();

            log.info("pDTO.toString() : " + pDTO.toString());

            testMapper.insertGCGadult(pDTO);

        }

        log.info(this.getClass().getName() + ".insertGCGadult End!");

    }

    @Override
    public List<TestDTO> getHMstudentList() throws Exception {

        log.info(this.getClass().getName() + ".getHMstudentList Start!");

        List<TestDTO> rList = testMapper.getHMstudentList();

        log.info(this.getClass().getName() + ".getHMstudentList End!");

        return rList;
    }

    @Override
    public List<TestDTO> getGCGstudentList() throws Exception {

        log.info(this.getClass().getName() + ".getGCGstudentList Start!");

        List<TestDTO> rList = testMapper.getGCGstudentList();

        log.info(this.getClass().getName() + ".getGCGstudentList End!");

        return rList;
    }

    @Override
    public List<TestDTO> getGCGadultList() throws Exception {

        log.info(this.getClass().getName() + ".getGCGadultList Start!");

        List<TestDTO> rList = testMapper.getGCGadultList();

        log.info(this.getClass().getName() + ".getGCGadultList End!");

        return rList;
    }

    @Override
    public UserInfoDTO getTesterInfo(UserInfoDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".getTesterInfo Start!");

        UserInfoDTO rDTO = Optional.ofNullable(testMapper.getTesterInfo(pDTO)).orElseGet(UserInfoDTO::new);

        log.info(this.getClass().getName() + ".getTesterInfo End!");

        return rDTO;
    }

    @Transactional
    @Override
    public void insertTestResult(TestDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".insertTestResult Start!");

        testMapper.insertTestResult(pDTO);

        log.info(this.getClass().getName() + ".insertTestResult End!");

    }

}
