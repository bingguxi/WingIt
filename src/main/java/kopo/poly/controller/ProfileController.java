package kopo.poly.controller;

import jakarta.servlet.http.HttpServletRequest;
import kopo.poly.dto.*;
import kopo.poly.service.IMongoService;
import kopo.poly.service.IProfileService;
import kopo.poly.util.CmmUtil;
import kopo.poly.util.EncryptUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequestMapping(value = "/profile")
@RequiredArgsConstructor
@Controller
public class ProfileController {

    private final IProfileService profileService;

    private final IMongoService mongoService;

    @GetMapping(value = "")
    public String getProfile(ModelMap modelMap, HttpSession session) throws Exception {

        log.info(this.getClass().getName() + ".getProfile Start!");

        String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));

        log.info("세션에서 받아온 userId : " + userId);

        UserInfoDTO pDTO = UserInfoDTO.builder().userId(userId).build();

        UserInfoDTO beforeDTO = Optional.ofNullable(profileService.getProfile(pDTO)).orElseGet(UserInfoDTO::new);

        log.info("DB에서 가져와 복호화 하기 전 이메일 : " + beforeDTO.getEmail());

        UserInfoDTO afterDTO = beforeDTO.toBuilder()
                                        .email(EncryptUtil.decAES128CBC(beforeDTO.getEmail()))
                                        .build();

        log.info("DB에서 가져와 복호화 하고나서 이메일 : " + afterDTO.getEmail());

        modelMap.addAttribute("rDTO", afterDTO);

        log.info("회원정보 조회 afterDTO.toString() : " + afterDTO.toString());

        log.info(this.getClass().getName() + ".getProfile End!");

        return "/myPage/profile";
    }


    @GetMapping(value = "/profileUpdate")
    public String profileUpdate(HttpSession session, ModelMap modelMap) throws Exception {

        log.info(this.getClass().getName() + ".profileUpdate Start!");

        String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));

        log.info("세션에서 받아온 userId : " + userId);

        UserInfoDTO pDTO = UserInfoDTO.builder().userId(userId).build();

        UserInfoDTO beforeDTO = Optional.ofNullable(profileService.getProfile(pDTO)).orElseGet(UserInfoDTO::new);

        log.info("DB에서 가져와 복호화 하기 전 이메일 : " + beforeDTO.getEmail());

        UserInfoDTO afterDTO = beforeDTO.toBuilder()
                .email(EncryptUtil.decAES128CBC(beforeDTO.getEmail()))
                .build();

        log.info("DB에서 가져와 복호화 하고나서 이메일 : " + afterDTO.getEmail());

        modelMap.addAttribute("rDTO", afterDTO);

        log.info("회원정보 조회 rDTO.toString() : " + afterDTO.toString());

        log.info(this.getClass().getName() + "profileUpdate End!");

        return "/myPage/profileUpdate";
    }


    @ResponseBody
    @PostMapping(value = "/profileUpdateProc")
    public MsgDTO updateProc(HttpSession session, HttpServletRequest request) {

        log.info(this.getClass().getName() + ".profileUpdateProc Start!");

        String msg = "";
        int result = 0;
        MsgDTO rDTO = null;

        try {

            String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));
            String userName = CmmUtil.nvl(request.getParameter("userName"));
            String nickname = CmmUtil.nvl(request.getParameter("nickname"));
            String password = CmmUtil.nvl(request.getParameter("password"));
            String identity = CmmUtil.nvl(request.getParameter("identity"));
            String gender = CmmUtil.nvl(request.getParameter("gender"));

            log.info("userId : " + userId);
            log.info("userName : " + userName);
            log.info("nickname : " + nickname);
            log.info("password : " + password);
            log.info("identity : " + identity);
            log.info("gender : " + gender);

            UserInfoDTO pDTO = UserInfoDTO.builder()
                    .userId(userId)
                    .userName(userName)
                    .nickname(nickname)
                    .password(EncryptUtil.encHashSHA256(password))
                    .identity(identity)
                    .gender(gender)
                    .build();

            profileService.updateProfile(pDTO);

            msg = "수정되었습니다.";
            result = 1;

            session.removeAttribute("SS_IDENTITY");
            session.setAttribute("SS_IDENTITY", identity);

        } catch (Exception e) {
            msg = "수정에 실패하였습니다. 다시 시도해주세요.";
            log.info(e.toString());
            e.printStackTrace();

        } finally {

            rDTO = MsgDTO.builder().msg(msg).result(result).build();

            log.info(this.getClass().getName() + ".profileUpdateProc End!");
        }

        return rDTO;
    }


    @ResponseBody
    @PostMapping(value = "/deleteUserInfo")
    public MsgDTO profileDelete(HttpServletRequest request, HttpSession session) throws Exception {

        log.info(this.getClass().getName() + ".deleteUserInfo Start!");

        String msg = ""; // 메시지 내용
        MsgDTO rDTO = null; // 결과 메시지 구조

        try {

            String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));

            log.info("SS_USER_ID : " + userId);

            UserInfoDTO pDTO = UserInfoDTO.builder().userId(userId).build();
            ChatDTO chatDTO = ChatDTO.builder().name(userId).build();

            // 회원정보 삭제하기 메서드 호출
            profileService.deleteUserInfo(pDTO);

            // 몽고디비 채팅내역 삭제 메서드 호출
            mongoService.deleteChatInfo(chatDTO);

            msg = "탈퇴되었습니다.";

        } catch (Exception e) {

            msg = "실패하였습니다. : " + e.getMessage();
            log.info(e.toString());
            e.printStackTrace();

        } finally {

            rDTO = MsgDTO.builder().msg(msg).build();

            // 만약 access_Token이 존재하면, 카카오 로그아웃 메소드 호출하기!
            /*if (session.getAttribute("access_Token") != null) {

                // 카카오 로그아웃 메소드 호출
                loginService.kakaoLogout((String) session.getAttribute("access_Token"));

                // 세션에 있는 접근토큰 삭제하기!
                session.removeAttribute("access_Token");
            }*/

            // 세션에 있는 유저아이디, 신분 삭제하기!
            session.removeAttribute("SS_USER_ID");
            session.removeAttribute("SS_IDENTITY");

            log.info("세션 삭제 후 session.getAttribute(\"SS_USER_ID\") : " + session.getAttribute("SS_USER_ID"));
            log.info("세션 삭제 후 session.getAttribute(\"SS_IDENTITY\") : " + session.getAttribute("SS_IDENTITY"));

            log.info(this.getClass().getName() + ".deleteUserInfo End!");

        }

        return rDTO;
    }

    @GetMapping(value = "getTestRecordList")
    public String getTestRecordList(HttpSession session, ModelMap model) throws Exception {

        log.info(this.getClass().getName() + ".getTestRecordList Start!");

        MsgDTO dto = null;
        String msg = "";

        String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));

        log.info("userId : " + userId);

        if (userId != null) {

            TestResultDTO pDTO = TestResultDTO.builder().userId(userId).build();

            List<TestResultDTO> rList = Optional.ofNullable(profileService.getTestRecordList(pDTO))
                    .orElseGet(ArrayList::new);

            log.info("rList : " + rList);

            // 모든 DTO 요소의 testTime 변수 값 뒤에 .0 붙는거 지우고 다시 rList에 추가하기!
            for (int i = 0; i < rList.size(); i++) {

                TestResultDTO testResultDTO = rList.get(i);

                try {

                    String testTime = testResultDTO.getTestTime();
                    log.info("testTime : " + testTime);

                    String newTestTime = testTime.replace(".0", "");
                    log.info("newTestTime : " + newTestTime);

                    // 수정된 객체를 리스트에 다시 저장
                    rList.set(i, testResultDTO.toBuilder().testTime(newTestTime).build());

                } catch (Exception e) {
                    log.error("Error converting date for user: " + testResultDTO, e);
                }
            }

            model.addAttribute("rList", rList);

        } else {

            msg = "관리자만 접근 가능합니다.";

        }

        dto = MsgDTO.builder().msg(msg).build();

        model.addAttribute("dto", dto);

        log.info(this.getClass().getName() + ".getTestRecordList End!");

        return "/myPage/testRecordList";

    }

    @ResponseBody
    @PostMapping(value = "/deleteTestRecord")
    public MsgDTO deleteTestRecord(HttpServletRequest request, HttpSession session) throws Exception {

        log.info(this.getClass().getName() + ".deleteTestRecord Start!");

        String msg = ""; // 메시지 내용
        MsgDTO rDTO = null; // 결과 메시지 구조

        try {

            String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));
            log.info("SS_USER_ID : " + userId);
            String testSeq = CmmUtil.nvl(request.getParameter("testSeq"));

            TestResultDTO pDTO = TestResultDTO.builder().userId(userId).testSeq(testSeq).build();

            // 진로심리검사 내역 삭제하기 메서드 호출
            profileService.deleteTestRecord(pDTO);

            msg = "삭제되었습니다.";

        } catch (Exception e) {

            msg = "실패하였습니다. : " + e.getMessage();
            log.info(e.toString());
            e.printStackTrace();

        } finally {

            rDTO = MsgDTO.builder().msg(msg).build();

            log.info(this.getClass().getName() + ".deleteTestRecord End!");

        }

        return rDTO;
    }

    @GetMapping(value = "getLikeJobList")
    public String getLikeJobList(HttpSession session, ModelMap model) throws Exception {

        log.info(this.getClass().getName() + ".getLikeJobList Start!");

        String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));

        log.info("userId : " + userId);

        LikeJobDTO pDTO = LikeJobDTO.builder().userId(userId).build();

        List<LikeJobDTO> rList = Optional.ofNullable(profileService.getLikeJobList(pDTO))
                .orElseGet(ArrayList::new);

        log.info("rList : " + rList);

        model.addAttribute("rList", rList);

        log.info(this.getClass().getName() + ".getLikeJobList End!");

        return "/myPage/likeJobList";

    }

}
