package kopo.poly.persistance.mapper;

import kopo.poly.dto.JobDTO;
import kopo.poly.dto.TestDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface IJobMapper {

    void insertJob(JobDTO pDTO) throws Exception; // 직업사전 삽입하기
    List<JobDTO> getJobList() throws Exception; // 직업사전 리스트 가져오기
    List<JobDTO> searchJob(JobDTO pDTO) throws Exception; // 키워드로 직업사전 리스트 가져오기

}
