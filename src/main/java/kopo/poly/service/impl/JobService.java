package kopo.poly.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import kopo.poly.dto.JobDTO;
import kopo.poly.dto.TestDTO;
import kopo.poly.persistance.mapper.IJobMapper;
import kopo.poly.persistance.mapper.ITestMapper;
import kopo.poly.service.IJobService;
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
public class JobService implements IJobService {

    private final IJobMapper jobMapper;

    @Value("${career-net.api.key}")
    private String apiKey;

    @Transactional
    @Override
    public void insertJob() throws Exception {

        log.info(this.getClass().getName() + ".insertJob Start!");

        String apiParam = "https://www.career.go.kr/cnet/openapi/getOpenApi?apiKey=" + apiKey + "&svcType=api&svcCode=JOB&contentType=json&gubun=job_dic_list&perPage=540";

        log.info("apiParam : " + apiParam);

        Map<String, String> headers = new HashMap<>();

        String json = NetworkUtil.get(apiParam, headers);

        Map<String, Object> rMap = new ObjectMapper().readValue(json, LinkedHashMap.class);

        Map<String, Object> dataSearchMap = (Map<String, Object>) rMap.get("dataSearch");

        List<Map<String, Object>> dataList = (List<Map<String, Object>>) dataSearchMap.get("content");

        List<JobDTO> pList = new ArrayList<>();

        for (Map<String, Object> dataMap : dataList) {
            JobDTO pDTO = JobDTO.builder()
                    .summary(String.valueOf(dataMap.get("summary")))
                    .jobdicSeq(String.valueOf(dataMap.get("jobdicSeq")))
                    .possibility(String.valueOf(dataMap.get("possibility")))
                    .build();

            log.info("pDTO.toString() : " + pDTO.toString());
            pList.add(pDTO);
        }


        List<JobDTO> rList = new ArrayList<>();

        for (JobDTO pDTO : pList) {

            String jobdicSeq = pDTO.getJobdicSeq();

            log.info("jobdicSeq : " + jobdicSeq);

            String apiParam2 = "https://www.career.go.kr/cnet/openapi/getOpenApi?apiKey=" + apiKey + "&svcType=api&svcCode=JOB_VIEW&contentType=json&gubun=job_dic_list&jobdicSeq=" + jobdicSeq;
            log.info(apiParam2);

            String json2 = NetworkUtil.get(apiParam2, headers);

            Map<String, Object> rMap2 = new ObjectMapper().readValue(json2, LinkedHashMap.class);

            Map<String, Object> dataSearchMap2 = (Map<String, Object>) rMap2.get("dataSearch");

            List<Map<String, Object>> contentList = (List<Map<String, Object>>) dataSearchMap2.get("content");

            for (Map<String, Object> content : contentList) {
                JobDTO rDTO = pDTO.toBuilder()
                        .jobName(String.valueOf(content.get("job")))
                        .aptitude(String.valueOf(content.get("aptitude")))
                        .build();

                List<Map<String, Object>> capacityMajorList = (List<Map<String, Object>>) content.get("capacity_major");

                for (Map<String, Object> capacityMajorMap : capacityMajorList) {
                    String capacity = (String) capacityMajorMap.get("capacity");

                    // capacity 값 처리
                    if (capacity != null) {

                        log.info("Capacity: " + capacity);
                        rDTO = rDTO.toBuilder().capacity(capacity).build();

                    }

                    List<Map<String,Object>> majorList = ( List<Map<String,Object>>) capacityMajorMap.get("major");

                    if (majorList != null) {

                        log.info("major : " + majorList.toString());

                        for(Map<String, Object> major : majorList) {
                            String rMajor = (String) major.get("MAJOR_NM");
                            log.info("rMajor : " + rMajor);
                            rDTO = rDTO.toBuilder().major(rMajor).build();
                        }
                    }
                }

                log.info("rDTO: " + rDTO.toString());

                List<Map<String, Object>> certificationList = (List<Map<String, Object>>) content.get("prepareway");

                for (Map<String, Object> certification : certificationList) {
                    Object certificationObj = certification.get("certification");

                    if (certificationObj instanceof List) {

                        List<String> certifications = (List<String>) certificationObj;

                        for (String c : certifications) {
                            rDTO = rDTO.toBuilder().certification(c).build();
                        }

                    } else if (certificationObj instanceof String) {

                        String certificationStr = (String) certificationObj;
                        rDTO = rDTO.toBuilder().certification(certificationStr).build();

                    }
                }

                log.info("rDTO.toString() : " + rDTO.toString());

                rList.add(rDTO);
            }

        }

        for(JobDTO dto : rList) {

            log.info("dto : "+ dto.toString());

            String jobName = "";
            String summary = "";
            String aptitude = "";
            String major = "";
            String capacity = "";
            String certification = "";
            String possibility = "";
            String jobdictSeq = "";

            if(dto.getJobName() != null) {
                jobName = dto.getJobName();
            }

            if(dto.getSummary() != null) {
                summary = dto.getSummary();
            }
            if(dto.getAptitude() != null) {
                aptitude = dto.getAptitude();
            }
            if(dto.getMajor() != null) {
                major = dto.getMajor();
            }
            if(dto.getCapacity() != null) {
                capacity = dto.getCapacity();
            }
            if(dto.getCertification() != null) {
                certification = dto.getCertification();
            }
            if(dto.getPossibility() != null) {
                possibility = dto.getPossibility();
            }
            if(dto.getJobdicSeq() != null) {
                jobdictSeq = dto.getJobdicSeq();
            }

            dto = dto.toBuilder(
            ).jobName(jobName
            ).summary(summary
            ).major(major
            ).capacity(capacity
            ).aptitude(aptitude
            ).certification(certification
            ).possibility(possibility
            ).jobdicSeq(jobdictSeq).build();

            jobMapper.insertJob(dto);

        }

        log.info(this.getClass().getName() + ".insertJob End!");

    }


    @Override
    public List<JobDTO> getJobList() throws Exception {

        log.info(this.getClass().getName() + ".getJobList Start!");

        List<JobDTO> rList = jobMapper.getJobList();

        log.info(this.getClass().getName() + ".getJobList End!");

        return rList;
    }

    @Override
    public List<JobDTO> searchJob(JobDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".searchJob Start!");

        List<JobDTO> rList = jobMapper.searchJob(pDTO);

        log.info(this.getClass().getName() + ".searchJob End!");

        return rList;
    }



}
