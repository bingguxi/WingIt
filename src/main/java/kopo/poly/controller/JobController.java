package kopo.poly.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kopo.poly.dto.JobDTO;
import kopo.poly.dto.MsgDTO;
import kopo.poly.dto.TestDTO;
import kopo.poly.dto.UserInfoDTO;
import kopo.poly.service.IJobService;
import kopo.poly.service.ILoginService;
import kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "job")
@Controller
public class JobController {

    private final IJobService jobService;
    private final ILoginService loginService;

    @GetMapping(value = "insertJob")
    public String insertJob() throws Exception {

        log.info(this.getClass().getName() + ".insertJob Start!");

        jobService.insertJob();

        log.info(this.getClass().getName() + ".insertJob End!");

        return "직업사전 삽입 완료!";
    }

    @GetMapping(value = "jobList")
    public String getJobList(ModelMap model, HttpSession session,
                             @RequestParam(name = "page", defaultValue = "1") int page,
                             @RequestParam(name = "jobName", defaultValue = "") String jobName) throws Exception {

        log.info(this.getClass().getName() + ".getJobList Start!");

        // 로그인 했는지 여부 확인하기
        String userId = (String) session.getAttribute("SS_USER_ID");

        log.info("메인 띄우기 전에 세션에서 가져온 userId : " + userId);

        UserInfoDTO cDTO = UserInfoDTO.builder().userId(userId).build();

        UserInfoDTO iDTO = Optional.ofNullable(loginService.getIdentity(cDTO))
                .orElseGet(UserInfoDTO::new);

        log.info("iDTO.getIdentity() : " + iDTO.getIdentity());

        MsgDTO dto = null;
        String msg = "";

        if (iDTO.getIdentity() == null) {

            msg = "로그인 후 이용하실 수 있습니다.";

        }
        dto = MsgDTO.builder().msg(msg).build();

        model.addAttribute("iDTO", iDTO);
        model.addAttribute("dto", dto);

        /* 로그인 했는지 여부 확인 끝! */

        if (iDTO.getIdentity() != null) {

            log.info("page : " + page);

            log.info("jobName : " + jobName);

            JobDTO pDTO = JobDTO.builder().jobName(jobName).build();

            List<JobDTO> rList = Optional.ofNullable(jobService.searchJob(pDTO)).orElseGet(ArrayList::new);

            /**페이징 시작 부분*/

            // 페이지당 보여줄 아이템 개수 정의
            int itemsPerPage = 10;

            // 페이지네이션을 위해 전체 아이템 개수 구하기
            int totalItems = rList.size();

            // 전체 페이지 개수 계산
            int totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);

            // 현재 페이지에 해당하는 아이템들만 선택하여 rList에 할당
            int fromIndex = (page - 1) * itemsPerPage;
            int toIndex = Math.min(fromIndex + itemsPerPage, totalItems);
            rList = rList.subList(fromIndex, toIndex);

            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);

            log.info(this.getClass().getName() + ".페이지 번호 : " + page);
            log.info("rList : " + rList.toString() );

            /**페이징 끝부분*/

            // 조회된 리스트 결과값 넣어주기
            model.addAttribute("rList", rList);

        }

        log.info(this.getClass().getName() + ".getJobList End!");

        return "/job/jobList";

    }
}
