package kopo.poly.controller;

import kopo.poly.dto.*;
import kopo.poly.service.ILoginService;
import kopo.poly.util.CmmUtil;
import kopo.poly.util.EncryptUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "login")
@Controller
public class LoginController {

    private final ILoginService loginService;

    @GetMapping(value = "")
    public String login() {

        log.info(this.getClass().getName() + ".login Start!");
        log.info(this.getClass().getName() + ".login End!");

        return "/user/login";
    }

/*
    @RequestMapping(value = "/kakaoLogin")
    public String kakaoLogin(@RequestParam("code") String code, HttpSession session) throws Exception {

        log.info(this.getClass().getName() + ".kakaoLogin Start!");

        String access_Token = loginService.getAccessToken(code);
        HashMap<String, Object> userInfo = loginService.getUserInfo(access_Token);
        System.out.println("login Controller : " + userInfo);

        //    클라이언트의 이메일이 존재할 때 세션에 해당 이메일과 토큰 등록
        if (userInfo.get("email") != null) {
            // session.setAttribute("userId", userInfo.get("email"));
            session.setAttribute("SS_USER_ID", userInfo.get("email"));
            session.setAttribute("access_Token", access_Token);
        }

        log.info("code : " + code);
        log.info("access_Token : " + access_Token);
        log.info("userInfo.get(\"email\") : " + userInfo.get("email"));

        log.info(this.getClass().getName() + ".kakaoLogin End!");

        return "/index";
    }
*/

    @PostMapping(value = "/loginProc")
    @ResponseBody
    public Map<String, Object> loginProc(HttpServletRequest request, HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        log.info(this.getClass().getName() + ".loginProc Start!");

        int res = 0;
        String msg = "";
        String url = "/index";

        UserInfoDTO pDTO = null;

        try {

            String userId = CmmUtil.nvl(request.getParameter("userId"));
            String password = CmmUtil.nvl(request.getParameter("password"));

            log.info("userId : " + userId);
            log.info("password : " + password);

            pDTO = UserInfoDTO.builder().userId(userId).password(EncryptUtil.encHashSHA256(password)).build();

            UserInfoDTO rDTO = loginService.getLogin(pDTO);

            log.info("rDTO.userId() : " + rDTO.getUserId());

            if (CmmUtil.nvl(rDTO.getUserId()).length() > 0) {

                res = 1;

                log.info("SS_USER_ID : " + userId);
                session.setAttribute("SS_USER_ID", userId);
                log.info("세션에 저장 후 session.getAttribute(\"SS_USER_ID\") : " + session.getAttribute("SS_USER_ID"));

                log.info("rDTO.getIdentity() : " + rDTO.getIdentity());
                String identity = rDTO.getIdentity();
                session.setAttribute("SS_IDENTITY", identity);
                log.info("세션에 저장 후 session.getAttribute(\"SS_IDENTITY\") : " + session.getAttribute("SS_IDENTITY"));

                msg = "로그인 성공! " + rDTO.getUserName() + "님 환영합니다.";

            } else {

                msg = "아이디와 비밀번호가 올바르지 않습니다.";
            }
        } catch (Exception e) {

            msg = "시스템 문제로 로그인이 실패했습니다.";
            log.info(e.toString());
            e.printStackTrace();

        }

        response.put("result", res);
        response.put("msg", msg);

        log.info(response.toString());

        log.info(this.getClass().getName() + ".loginProc End!");

        return response;
    }


    @GetMapping(value = "/findId")
    public String findId() {

        log.info(this.getClass().getName() + ".findId Start!");
        log.info(this.getClass().getName() + ".findId End!");

        return "/user/findId";
    }


    @ResponseBody
    @PostMapping(value = "/findIdProc")
    public MsgDTO findIdProc(HttpServletRequest request) throws Exception {

        log.info(this.getClass().getName() + ".findIdProc Start!");

        MsgDTO dto = null;

        String userName = CmmUtil.nvl(request.getParameter("userName")); // 이름
        String email = CmmUtil.nvl(request.getParameter("email")); // 이메일

        log.info("userName : " + userName);
        log.info("email : " + email);

        UserInfoDTO pDTO = UserInfoDTO.builder()
                .userName(userName)
                .email(EncryptUtil.encAES128CBC(email))
                .build();

        UserInfoDTO rDTO = Optional.ofNullable(loginService.findIdProc(pDTO))
                .orElseGet(() -> UserInfoDTO.builder().build());

        if (rDTO.getUserId() == null) {

            dto = MsgDTO.builder().msg("정보가 존재하지 않습니다.").build();

        } else {

            dto = MsgDTO.builder().msg("회원님의 아이디는 " + rDTO.getUserId() + " 입니다.").build();

        }

        log.info("rDTO.getUserId() : " + rDTO.getUserId());

        log.info(this.getClass().getName() + ".findIdProc End!");

        return dto;
    }

    // 비밀번호 찾기
    @GetMapping(value = "/findPwd")
    public String findPassword(HttpSession session) {
        log.info(this.getClass().getName() + ".findPwd Start!");

        // 강제 URL 입력 등 오는 경우가 있어 세션 삭제
        // 비밀번호 재생성하는 화면은 보안을 위해 생성한 NEW_PASSWORD 세션 삭제
        session.setAttribute("NEW_PASSWORD", "");
        session.removeAttribute("NEW_PASSWORD");

        log.info(this.getClass().getName() + ".findPwd End!");

        return "/user/findPwd";
    }


    // 아이디, 이름, 이메일 모두 일치하는지 여부 확인 후 생성된 임시 비밀번호 전송
    @PostMapping(value = "/findPwdProc")
    public String newPasswordProc(HttpServletRequest request, ModelMap model) throws Exception {

        log.info(getClass().getName() + ".newPasswordProc Start!");

        // 입력받은 값을 변수에 저장하기
        String userId = CmmUtil.nvl(request.getParameter("userId")); // 아이디
        String userName = CmmUtil.nvl(request.getParameter("userName")); // 이름
        String email = CmmUtil.nvl(request.getParameter("email")); // 이메일

        log.info("userId : " + userId);
        log.info("userName : " + userName);
        log.info("email : " + email);

        UserInfoDTO pDTO = UserInfoDTO.builder()
                .userId(userId)
                .userName(userName)
                .email(EncryptUtil.encAES128CBC(email)).build();

        String msg = "";
        String url = "";

        int res = loginService.findPwdProc(pDTO);

        if (res == 1) {

            msg = "가입하신 메일로 임시 비밀번호를 전송하였습니다.";
            url = "/login";

        } else {

            msg = "이메일이 등록되어 있지 않습니다. 다시 한번 확인해주세요.";
            url = "/login/findPwd";

        }

        model.addAttribute("msg", msg);
        model.addAttribute("url", url);

        log.info(getClass().getName() + ".newPasswordProc End!");

        return "/redirect";
    }


    @GetMapping (value="/logout")
    public String logout(HttpSession session) throws Exception {

        log.info(this.getClass().getName() + ".logout Start!");

/*
        // 만약 access_Token이 존재하면, 카카오 로그아웃 메소드 호출하기!
        if (session.getAttribute("access_Token") != null) {

            // 카카오 로그아웃 메소드 호출
            loginService.kakaoLogout((String) session.getAttribute("access_Token"));

            // 세션에 있는 접근토큰 삭제하기!
            session.removeAttribute("access_Token");
        }
*/

        // 세션에 있는 유저아이디, 신분 삭제하기!
        session.removeAttribute("SS_USER_ID");
        session.removeAttribute("SS_IDENTITY");

        log.info("세션 삭제 후 session.getAttribute(\"SS_USER_ID\") : " + session.getAttribute("SS_USER_ID"));
        log.info("세션 삭제 후 session.getAttribute(\"SS_IDENTITY\") : " + session.getAttribute("SS_IDENTITY"));

        log.info(this.getClass().getName() + ".logout End!");

        return "user/login";
    }

}
