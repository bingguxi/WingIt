package kopo.poly.service;

import kopo.poly.dto.JobDTO;
import kopo.poly.dto.TestDTO;

import java.util.List;

public interface IJobService {

    void insertJob() throws Exception; // 직업사전 삽입하기
    List<JobDTO> getJobList() throws Exception; // 직업사전 리스트 가져오기
    List<JobDTO> searchJob(JobDTO pDTO) throws Exception; // 키워드로 직업사전 리스트 가져오기

}
