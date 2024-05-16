package kopo.poly.persistance.mapper;

import kopo.poly.dto.TestDTO;
import kopo.poly.dto.UserInfoDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ITestMapper {

    void insertHMstudent(TestDTO pDTO) throws Exception; // 직업흥미검사(K) 고등학생 문항 삽입하기 (일회성)

    void insertGCGstudent(TestDTO pDTO) throws Exception; // 직업가치관검사 고등학생 문항 삽입하기 (일회성)

    void insertGCGadult(TestDTO pDTO) throws Exception; // 직업가치관검사 일반,대학생 문항 삽입하기 (일회성)

    List<TestDTO> getHMstudentList() throws Exception; // 직업흥미검사(K) 고등학생 문항 리스트 가져오기

    List<TestDTO> getGCGstudentList() throws Exception; // 직업가치관검사 고등학생 문항 리스트 가져오기

    List<TestDTO> getGCGadultList() throws Exception; // 직업가치관검사 일반,대학생 문항 리스트 가져오기

    UserInfoDTO getTesterInfo(UserInfoDTO pDTO) throws Exception; // 검시자의 성별코드, 학년 정보 가져오기

    void insertTestResult(TestDTO pDTO) throws Exception; // 검사 결과 내역 저장하기

}
