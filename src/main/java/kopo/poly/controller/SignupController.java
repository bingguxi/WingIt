package kopo.poly.controller;

import jakarta.servlet.http.HttpServletRequest;
import kopo.poly.dto.MsgDTO;
import kopo.poly.dto.UserInfoDTO;
import kopo.poly.service.ISignupService;
import kopo.poly.util.CmmUtil;
import kopo.poly.util.EncryptUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Optional;

@Slf4j
@RequestMapping(value = "signup")
@RequiredArgsConstructor
@Controller
public class SignupController {

    // @RequiredArgsConstructor 를 통해 메모리에 올라간 서비스 객체를 Controller에서 사용할 수 있게 주입함
    private final ISignupService signupService;

    /**
     * 회원가입 화면으로 이동
     */
    @GetMapping(value = "")
    public String signup() {

        log.info(this.getClass().getName() + ".signup Start!");
        log.info(this.getClass().getName() + ".signup End!");

        return "/user/userRegForm";
    }


    /**
     * 회원 가입 전 아이디 중복체크하기(Ajax를 통해 입력한 아이디 정보 받음)
     */
    @ResponseBody
    @PostMapping(value = "getUserIdExists")
    public UserInfoDTO getUserIdExists(HttpServletRequest request) throws Exception {

        log.info(this.getClass().getName() + ".getUserIdExists Start!");

        String userId = CmmUtil.nvl(request.getParameter("userId")); // 회원아이디

        log.info("userId : " + userId);

        // Builder 통한 값 저장
        UserInfoDTO pDTO = UserInfoDTO.builder().userId(userId).build();

        // 회원아이디를 통해 중복된 아이디인지 조회
        UserInfoDTO rDTO = Optional.ofNullable(signupService.getUserIdExists(pDTO))
                .orElseGet(() -> UserInfoDTO.builder().build());

        log.info(this.getClass().getName() + ".getUserIdExists End!");

        return rDTO;
    }

    /**
     * 회원 가입 전 닉네임 중복체크하기(Ajax를 통해 입력한 닉네임 정보 받음)
     */
    @ResponseBody
    @PostMapping(value = "getNicknameExists")
    public UserInfoDTO getNicknameExists(HttpServletRequest request) throws Exception {

        log.info(this.getClass().getName() + ".getNicknameExists Start!");

        String nickname = CmmUtil.nvl(request.getParameter("nickname")); // 닉네임

        log.info("nickname : " + nickname);

        // Builder 통한 값 저장
        UserInfoDTO pDTO = UserInfoDTO.builder().nickname(nickname).build();

        // 입력 받은 닉네임을 통해 중복된 닉네임인지 조회
        UserInfoDTO rDTO = Optional.ofNullable(signupService.getNicknameExists(pDTO))
                .orElseGet(() -> UserInfoDTO.builder().build());

        log.info(this.getClass().getName() + ".getNicknameExists End!");

        return rDTO;
    }

    @ResponseBody
    @PostMapping(value = "getEmailExists")
    public UserInfoDTO getEmailExists(HttpServletRequest request) throws Exception {

        log.info(this.getClass().getName() + ".getEmailExists Start!");

        String email = CmmUtil.nvl(request.getParameter("email")); // 회원 아이디

        log.info("email : " + email);

        UserInfoDTO pDTO = UserInfoDTO.builder().email(EncryptUtil.encAES128CBC(email)).build();

        // 입력된 이메일이 중복된 이메일인지 조회
        UserInfoDTO rDTO = Optional.ofNullable(signupService.getEmailExists(pDTO))
                .orElseGet(() -> UserInfoDTO.builder().build());

        log.info(this.getClass().getName() + ".getEmailExists End!");

        return rDTO;
    }

    @ResponseBody
    @PostMapping(value = "/signupProc")
    public MsgDTO signupProc(HttpServletRequest request, ModelMap modelMap) throws Exception {

        log.info(this.getClass().getName() + ".signupProc start!");

        int res;
        String msg = "";

        UserInfoDTO pDTO = null;
        MsgDTO dto = null;

        try {
            String userId = CmmUtil.nvl(request.getParameter("userId")); // 아이디
            String userName = CmmUtil.nvl(request.getParameter("userName")); // 이름
            String nickname = CmmUtil.nvl(request.getParameter("nickname")); // 닉네임
            String password = CmmUtil.nvl(request.getParameter("password")); // 비밀번호
            String identity = CmmUtil.nvl(request.getParameter("identity")); // 학년
            String gender = CmmUtil.nvl(request.getParameter("gender")); // 성별
            String email = CmmUtil.nvl(request.getParameter("email")); // 이메일

            log.info("userId : " + userId);
            log.info("userName : " + userName);
            log.info("nickname : " + nickname);
            log.info("password : " + password);
            log.info("identity : " + identity);
            log.info("gender : " + gender);
            log.info("email : " + email);

            pDTO = UserInfoDTO.builder()
                    .userId(userId)
                    .userName(userName)
                    .nickname(nickname)
                    .password(EncryptUtil.encHashSHA256(password))
                    .identity(identity)
                    .gender(gender)
                    .email(EncryptUtil.encAES128CBC(email))
                    .build();

            res = signupService.insertUserInfo(pDTO);

            log.info("회원가입 결과(res) : " + res);

            if (res == 1) {

                dto = MsgDTO.builder().msg("회원가입 완료! 로그인 후 서비스를 이용하실 수 있습니다.").result(1).build();

            } else {
                msg = "오류로 인해 회원가입이 실패하였습니다. 다시 시도해주세요.";
                dto = MsgDTO.builder().msg(msg).build();
            }

        } catch (Exception e) {
            // 저장이 실패되면 사용자에게 보여줄 메시지
            msg = "실패하였습니다. : " + e;
            log.info(e.toString());
            e.printStackTrace();
            dto = MsgDTO.builder().msg(msg).build();

        }

        log.info(this.getClass().getName() + ".signupProc End!");

        return dto;
    }

}
