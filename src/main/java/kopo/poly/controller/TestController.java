package kopo.poly.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kopo.poly.dto.MsgDTO;
import kopo.poly.dto.TestDTO;
import kopo.poly.dto.UserInfoDTO;
import kopo.poly.service.ILoginService;
import kopo.poly.service.ITestService;
import kopo.poly.util.CmmUtil;
import kopo.poly.util.NetworkUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "test")
@Controller
public class TestController {

    private final ITestService testService;
    private final ILoginService loginService;

    @Value("${career-net.api.key}")
    private String apiKey;

    @GetMapping(value = "insertHMstudent")
    public String insertHMstudent() throws Exception {

        log.info(this.getClass().getName() + ".insertHMstudent Start!");

        testService.insertHMstudent();

        log.info(this.getClass().getName() + ".insertHMstudent End!");

        return "직업흥미검사(K) 고등학생 항목 삽입 완료!";
    }

    @GetMapping(value = "insertGCGstudent")
    public String insertGCGstudent() throws Exception {

        log.info(this.getClass().getName() + ".insertGCGstudent Start!");

        testService.insertGCGstudent();

        log.info(this.getClass().getName() + ".insertGCGstudent End!");

        return "직업가치관검사 고등학생 항목 삽입 완료!";
    }

    @GetMapping(value = "insertGCGadult")
    public String insertGCGadult() throws Exception {

        log.info(this.getClass().getName() + ".insertGCGadult Start!");

        testService.insertGCGadult();

        log.info(this.getClass().getName() + ".insertGCGadult End!");

        return "직업가치관검사 일반,대학생 항목 삽입 완료!";
    }


    @GetMapping(value = "testHMstudent")
    public String testHMstudent(ModelMap model, @RequestParam(defaultValue = "1") int page, HttpSession session) throws Exception {

        log.info(this.getClass().getName() + ".testHMstudent Start!");

        // 세션에 남아있는 URL 값 지우기
        session.removeAttribute("URL");

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

            List<TestDTO> rList = Optional.ofNullable(testService.getHMstudentList()).orElseGet(ArrayList::new);

            // 조회된 리스트 결과값 넣어주기
            model.addAttribute("rList", rList);

            String gender = "";

            UserInfoDTO pDTO = UserInfoDTO.builder().userId(userId).build();

            UserInfoDTO rDTO = Optional.ofNullable(testService.getTesterInfo(pDTO)).orElseGet(UserInfoDTO::new);

            if (Objects.equals(rDTO.getGender(), "남")) {

                gender = "100323";

            } else if (Objects.equals(rDTO.getGender(), "여")) {

                gender = "100324";

            }

            log.info("gender : " + gender);

            // startDtm 현재시간을 ex.1550466291034 요렇게 Unix 시간 값으로 대입
            long currentTimeMillis = System.currentTimeMillis();
            String startDtm = String.valueOf(currentTimeMillis);

            log.info("startDtm : " + startDtm);

            model.addAttribute("gender", gender);
            model.addAttribute("startDtm", startDtm);

        }


        /* *//**페이징 시작 부분*//*

        // 페이지당 보여줄 아이템 개수 정의
        int itemsPerPage = 20;

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

        *//**페이징 끝부분*/


        log.info(this.getClass().getName() + ".testHMstudent End!");

        return "/test/testHMstudent";
    }

    @ResponseBody
    @PostMapping(value = "testHMstudentSaveProc")
    public MsgDTO testHMstudentSaveProc(HttpServletRequest request, HttpSession session) throws Exception {

        log.info(this.getClass().getName() + ".testHMstudentSaveProc Start!");

        MsgDTO dto = null;

        String userId = (String) session.getAttribute("SS_USER_ID");
        String qestrnSeq = CmmUtil.nvl(request.getParameter("qestrnSeq"));
        String url = CmmUtil.nvl(request.getParameter("url"));

        log.info("userId : " + userId);
        log.info("qestrnSeq : " + qestrnSeq);
        log.info("url : " + url);

        TestDTO pDTO = TestDTO.builder().userId(userId).qestrnSeq(qestrnSeq).url(url).build();

        testService.insertTestResult(pDTO);

        session.setAttribute("URL", url);
        log.info("세션에 올라간 url 값 : " + session.getAttribute("URL"));

        String msg = "검사 결과가 저장되었습니다.";

        dto = MsgDTO.builder().msg(msg).build();

        log.info(this.getClass().getName() + ".testHMstudentSaveProc End!");

        // 이거 해도 ajax로 호출한 컨트롤러 메서드라 페이지 이동 안된대...
        // 그래서 프론트단 호출 성공 부분에 location.href 또 따로 해줘야 됨
        return dto;
    }

    @GetMapping(value = "testHMstudentResult")
    public String testHMstudentResult(HttpSession session, ModelMap model) throws Exception {

        log.info(this.getClass().getName() + ".testHMstudentResult Start!");

        // 네비게이션 바를 위한 세션 로그인 정보 확인하기
        String userId = (String) session.getAttribute("SS_USER_ID");

        log.info("메인 띄우기 전에 세션에서 가져온 userId : " + userId);

        UserInfoDTO cDTO = UserInfoDTO.builder().userId(userId).build();

        UserInfoDTO iDTO = Optional.ofNullable(loginService.getIdentity(cDTO))
                .orElseGet(UserInfoDTO::new);

        log.info("iDTO.getIdentity() : " + iDTO.getIdentity());

        model.addAttribute("iDTO", iDTO);

        log.info(this.getClass().getName() + ".testHMstudentResult End!");

        return "test/testHMstudentResult";

    }

    @GetMapping(value = "testGCGstudent")
    public String testGCGstudent(ModelMap model, @RequestParam(defaultValue = "1") int page, HttpSession session) throws Exception {

        log.info(this.getClass().getName() + ".testGCGstudent Start!");

        // 세션에 남아있는 URL 값 지우기
        session.removeAttribute("URL");

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

            List<TestDTO> rList = Optional.ofNullable(testService.getGCGstudentList()).orElseGet(ArrayList::new);

            String gender = "";

            UserInfoDTO pDTO = UserInfoDTO.builder().userId(userId).build();

            UserInfoDTO rDTO = Optional.ofNullable(testService.getTesterInfo(pDTO)).orElseGet(UserInfoDTO::new);

            if (Objects.equals(rDTO.getGender(), "남")) {

                gender = "100323";

            } else if (Objects.equals(rDTO.getGender(), "여")) {

                gender = "100324";

            }

            log.info("gender : " + gender);

            // startDtm 현재시간을 ex.1550466291034 요렇게 Unix 시간 값으로 대입
            long currentTimeMillis = System.currentTimeMillis();
            String startDtm = String.valueOf(currentTimeMillis);

            log.info("startDtm : " + startDtm);

            model.addAttribute("gender", gender);
            model.addAttribute("startDtm", startDtm);


            /* *//**페이징 시작 부분*//*

        // 페이지당 보여줄 아이템 개수 정의
        int itemsPerPage = 20;

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

        *//**페이징 끝부분*/

            // 조회된 리스트 결과값 넣어주기
            model.addAttribute("rList", rList);

        }


        log.info(this.getClass().getName() + ".testGCGstudent End!");

        return "/test/testGCGstudent";
    }

    @ResponseBody
    @PostMapping(value = "testGCGstudentSaveProc")
    public MsgDTO testGCGstudentSaveProc(HttpServletRequest request, HttpSession session) throws Exception {

        log.info(this.getClass().getName() + ".testGCGstudentSaveProc Start!");

        MsgDTO dto = null;

        String userId = (String) session.getAttribute("SS_USER_ID");
        String qestrnSeq = CmmUtil.nvl(request.getParameter("qestrnSeq"));
        String url = CmmUtil.nvl(request.getParameter("url"));

        log.info("userId : " + userId);
        log.info("qestrnSeq : " + qestrnSeq);
        log.info("url : " + url);

        TestDTO pDTO = TestDTO.builder().userId(userId).qestrnSeq(qestrnSeq).url(url).build();

        testService.insertTestResult(pDTO);

        session.setAttribute("URL", url);
        log.info("세션에 올라간 url 값 : " + session.getAttribute("URL"));

        String msg = "검사 결과가 저장되었습니다.";

        dto = MsgDTO.builder().msg(msg).build();

        log.info(this.getClass().getName() + ".testGCGstudentSaveProc End!");

        // 이거 해도 ajax로 호출한 컨트롤러 메서드라 페이지 이동 안된대...
        // 그래서 프론트단 호출 성공 부분에 location.href 또 따로 해줘야 됨
        return dto;
    }

    @GetMapping(value = "/testGCGstudentResult")
    public String testGCGstudentResult(HttpSession session, ModelMap model) throws Exception {

        log.info(this.getClass().getName() + ".testGCGstudentResult Start!");

        // 네비게이션 바를 위한 세션 로그인 정보 확인하기
        String userId = (String) session.getAttribute("SS_USER_ID");

        log.info("메인 띄우기 전에 세션에서 가져온 userId : " + userId);

        UserInfoDTO cDTO = UserInfoDTO.builder().userId(userId).build();

        UserInfoDTO iDTO = Optional.ofNullable(loginService.getIdentity(cDTO))
                .orElseGet(UserInfoDTO::new);

        log.info("iDTO.getIdentity() : " + iDTO.getIdentity());

        model.addAttribute("iDTO", iDTO);

        log.info(this.getClass().getName() + ".testGCGstudentResult End!");

        return "test/testGCGstudentResult";

    }

    @GetMapping(value = "testGCGadult")
    public String testGCGadult(ModelMap model, @RequestParam(defaultValue = "1") int page, HttpSession session) throws Exception {

        log.info(this.getClass().getName() + ".testGCGadult Start!");

        // 세션에 남아있는 URL 값 지우기
        session.removeAttribute("URL");

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

            List<TestDTO> rList = Optional.ofNullable(testService.getGCGadultList()).orElseGet(ArrayList::new);

            String gender = "";
            String identity = "";

            UserInfoDTO pDTO = UserInfoDTO.builder().userId(userId).build();

            UserInfoDTO rDTO = Optional.ofNullable(testService.getTesterInfo(pDTO)).orElseGet(UserInfoDTO::new);

            if (Objects.equals(rDTO.getGender(), "남")) {

                gender = "100323";

            } else if (Objects.equals(rDTO.getGender(), "여")) {

                gender = "100324";

            }

            if (Objects.equals(rDTO.getIdentity(), "대학생")) {

                identity = "100208";

            } else if (Objects.equals(rDTO.getIdentity(), "일반")) {

                identity = "100209";

            }

            log.info("gender : " + gender);
            log.info("identity : " + identity);

            // startDtm 현재시간을 ex.1550466291034 요렇게 Unix 시간 값으로 대입
            long currentTimeMillis = System.currentTimeMillis();
            String startDtm = String.valueOf(currentTimeMillis);

            log.info("startDtm : " + startDtm);

            model.addAttribute("gender", gender);
            model.addAttribute("identity", identity);
            model.addAttribute("startDtm", startDtm);


            /* *//**페이징 시작 부분*//*

        // 페이지당 보여줄 아이템 개수 정의
        int itemsPerPage = 20;

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

        *//**페이징 끝부분*/

            // 조회된 리스트 결과값 넣어주기
            model.addAttribute("rList", rList);

        }

        log.info(this.getClass().getName() + ".testGCGadult End!");

        return "/test/testGCGadult";
    }

    @ResponseBody
    @PostMapping(value = "testGCGadultSaveProc")
    public MsgDTO testGCGadultSaveProc(HttpServletRequest request, HttpSession session) throws Exception {

        log.info(this.getClass().getName() + ".testGCGadultSaveProc Start!");

        MsgDTO dto = null;

        String userId = (String) session.getAttribute("SS_USER_ID");
        String qestrnSeq = CmmUtil.nvl(request.getParameter("qestrnSeq"));
        String url = CmmUtil.nvl(request.getParameter("url"));

        log.info("userId : " + userId);
        log.info("qestrnSeq : " + qestrnSeq);
        log.info("url : " + url);

        TestDTO pDTO = TestDTO.builder().userId(userId).qestrnSeq(qestrnSeq).url(url).build();

        testService.insertTestResult(pDTO);

        session.setAttribute("URL", url);
        log.info("세션에 올라간 url 값 : " + session.getAttribute("URL"));

        String msg = "검사 결과가 저장되었습니다.";

        dto = MsgDTO.builder().msg(msg).build();

        log.info(this.getClass().getName() + ".testGCGadultSaveProc End!");

        // 이거 해도 ajax로 호출한 컨트롤러 메서드라 페이지 이동 안된대...
        // 그래서 프론트단 호출 성공 부분에 location.href 또 따로 해줘야 됨
        return dto;
    }

    @GetMapping(value = "/testGCGadultResult")
    public String testGCGadultResult(HttpSession session, ModelMap model) throws Exception {

        log.info(this.getClass().getName() + ".testGCGadultResult Start!");

        // 네비게이션 바를 위한 세션 로그인 정보 확인하기
        String userId = (String) session.getAttribute("SS_USER_ID");

        log.info("메인 띄우기 전에 세션에서 가져온 userId : " + userId);

        UserInfoDTO cDTO = UserInfoDTO.builder().userId(userId).build();

        UserInfoDTO iDTO = Optional.ofNullable(loginService.getIdentity(cDTO))
                .orElseGet(UserInfoDTO::new);

        log.info("iDTO.getIdentity() : " + iDTO.getIdentity());

        model.addAttribute("iDTO", iDTO);

        log.info(this.getClass().getName() + ".testGCGadultResult End!");

        return "/test/testGCGadultResult";

    }

}
