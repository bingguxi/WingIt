package kopo.poly.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kopo.poly.dto.*;
import kopo.poly.service.IAdminService;
import kopo.poly.service.IMongoService;
import kopo.poly.util.CmmUtil;
import kopo.poly.util.EncryptUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequestMapping(value = "/admin")
@RequiredArgsConstructor
@Controller
public class AdminController {

    private final IAdminService adminService;

    private final IMongoService mongoService;

    @GetMapping(value = "getUserList")
    public String getUserList(ModelMap model, HttpSession session,
                              @RequestParam(name = "page", defaultValue = "1") int page) throws Exception {

        log.info(this.getClass().getName() + ".getUserList Start!");

        String admin = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));

        log.info("세션에서 받아온 admin : " + admin);

        List<UserInfoDTO> rList = Optional.ofNullable(adminService.getUserList())
                .orElseGet(ArrayList::new);

        log.info("rList : " + rList);

        // 모든 DTO 요소의 regDt 변수 값 뒤에 .0 붙는거 지우고 다시 rList에 추가하기!
        for (int i = 0; i < rList.size(); i++) {

            UserInfoDTO userInfoDTO = rList.get(i);

            try {

                String regDt = userInfoDTO.getRegDt();
                log.info("regDt : " + regDt);

                String newRegDt = regDt.replace(".0", "");
                log.info("newRegDt : " + newRegDt);

                // 수정된 객체를 리스트에 다시 저장
                rList.set(i, userInfoDTO.toBuilder().regDt(newRegDt).build());

            } catch (Exception e) {
                log.error("Error converting date for user: " + userInfoDTO, e);
            }
        }

        model.addAttribute("rList", rList);

        /**페이징 시작 부분*/

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
//            log.info("rList : " + rList.toString() );

        /**페이징 끝부분*/

        log.info(this.getClass().getName() + ".getUserList End!");

        return "/admin/userList";

    }

    @GetMapping(value = "getUserInfo")
    public String getUserInfo(ModelMap model, HttpSession session, HttpServletRequest request) throws Exception {

        log.info(this.getClass().getName() + ".getUserInfo Start!");

        String admin = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));
        log.info("세션에서 받아온 admin : " + admin);

        String userId = CmmUtil.nvl(request.getParameter("userId"));
        log.info("조회하고자 하는 userId : " + userId);

        UserInfoDTO pDTO = UserInfoDTO.builder().userId(userId).build();

        UserInfoDTO beforeDTO = Optional.ofNullable(adminService.getUserInfo(pDTO)).orElseGet(UserInfoDTO::new);

        log.info("DB에서 가져와 복호화 하기 전 이메일 : " + beforeDTO.getEmail());

        UserInfoDTO afterDTO = beforeDTO.toBuilder()
                .email(EncryptUtil.decAES128CBC(beforeDTO.getEmail()))
                .build();

        log.info("DB에서 가져와 복호화 하고나서 이메일 : " + afterDTO.getEmail());

        model.addAttribute("rDTO", afterDTO);

        log.info("회원정보 조회 afterDTO : " + afterDTO);

        log.info(this.getClass().getName() + ".getUserInfo End!");

        return "/admin/userInfo";
    }


    @GetMapping(value = "/updateUserInfo")
    public String updateUserInfo(HttpSession session, ModelMap model, HttpServletRequest request) throws Exception {

        log.info(this.getClass().getName() + ".updateUserInfo Start!");

        String admin = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));

        log.info("세션에서 받아온 admin : " + admin);
        String userId = CmmUtil.nvl(request.getParameter("userId"));

        log.info("조회하고자 하는 userId : " + userId);
        UserInfoDTO pDTO = UserInfoDTO.builder().userId(userId).build();

        UserInfoDTO beforeDTO = Optional.ofNullable(adminService.getUserInfo(pDTO)).orElseGet(UserInfoDTO::new);

        log.info("DB에서 가져와 복호화 하기 전 이메일 : " + beforeDTO.getEmail());

        UserInfoDTO afterDTO = beforeDTO.toBuilder()
                .email(EncryptUtil.decAES128CBC(beforeDTO.getEmail()))
                .build();

        log.info("DB에서 가져와 복호화 하고나서 이메일 : " + afterDTO.getEmail());

        model.addAttribute("rDTO", afterDTO);

        log.info("회원정보 조회 afterDTO : " + afterDTO.toString());

        log.info(this.getClass().getName() + ".updateUserInfo End!");

        return "/admin/updateUserInfo";
    }


    @ResponseBody
    @PostMapping(value = "/updateUserInfoProc")
    public MsgDTO updateUserInfoProc(HttpServletRequest request) {

        log.info(this.getClass().getName() + ".updateUserInfoProc Start!");

        String msg = "";
        int result = 0;
        MsgDTO rDTO = null;

        try {

            String userId = CmmUtil.nvl(request.getParameter("userId"));
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

            adminService.updateUserInfo(pDTO);

            msg = userId + " 회원의 정보가 수정되었습니다.";
            result = 1;

        } catch (Exception e) {
            msg = "수정에 실패하였습니다. 다시 시도해주세요.";
            log.info(e.toString());
            e.printStackTrace();

        } finally {

            rDTO = MsgDTO.builder().msg(msg).result(result).build();

        }

        log.info(this.getClass().getName() + ".updateUserInfoProc End!");

        return rDTO;
    }

    @ResponseBody
    @PostMapping(value = "/deleteUserInfo")
    public MsgDTO deleteUserInfo(HttpServletRequest request) throws Exception {

        log.info(this.getClass().getName() + ".deleteUserInfo Start!");

        String msg = ""; // 메시지 내용
        MsgDTO rDTO = null; // 결과 메시지 구조

        String userId = CmmUtil.nvl(request.getParameter("userId"));
        log.info("userId : " + userId);

        try {

            UserInfoDTO pDTO = UserInfoDTO.builder().userId(userId).build();

            ChatDTO chatDTO = ChatDTO.builder().name(userId).build();

            // 회원정보 삭제하기 메서드 호출
            adminService.deleteUserInfo(pDTO);

            // 몽고디비 채팅내역 삭제 메서드 호출
            mongoService.deleteChatInfo(chatDTO);

            msg = userId + " 님의 회원정보를 삭제하였습니다.";

        } catch (Exception e) {

            msg = userId + " 님의 회원정보 삭제에 실패하였습니다. : " + e.getMessage();
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

        }

        rDTO = MsgDTO.builder().msg(msg).build();

        log.info(this.getClass().getName() + ".deleteUserInfo End!");

        return rDTO;
    }

    @GetMapping(value = "/getCSList")
    public String getCSList(ModelMap model,
                            @RequestParam(name = "page", defaultValue = "1") int page) throws Exception {

        log.info(this.getClass().getName() + ".getCSList Start!");

        List<ChatDTO> rList = Optional.ofNullable(mongoService.getChatList())
                .orElseGet(ArrayList::new);

        model.addAttribute("rList", rList);

        /**페이징 시작 부분*/

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
//            log.info("rList : " + rList.toString() );

        /**페이징 끝부분*/

        log.info(this.getClass().getName() + ".getCSList End!");

        return "/admin/adminCSList";

    }

    @GetMapping(value = "CSChatroom")
    public String CSChatroom(ModelMap model, HttpServletRequest request) throws Exception {

        log.info(this.getClass().getName() + ".CSChatroom Start!");

        String userId = CmmUtil.nvl(request.getParameter("userId"));
        log.info("조회하고자 하는 userId : " + userId);

        ChatDTO pDTO = ChatDTO.builder().name(userId).build();

        List<ChatDTO> rList = Optional.ofNullable(mongoService.getChats(pDTO)).orElseGet(ArrayList::new);

        model.addAttribute("userId", userId);
        model.addAttribute("rList", rList);

        log.info(this.getClass().getName() + ".CSChatroom End!");

        return "/admin/adminCSChatroom";
    }

}
