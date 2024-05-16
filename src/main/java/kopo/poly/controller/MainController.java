package kopo.poly.controller;

import jakarta.servlet.http.HttpSession;
import kopo.poly.dto.MsgDTO;
import kopo.poly.dto.UserInfoDTO;
import kopo.poly.service.ILoginService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MainController {

    private final ILoginService loginService;

    @GetMapping(value = "/index")
    public String index() {

        log.info(this.getClass().getName() + ".index Start!");
        log.info(this.getClass().getName() + ".index End!");

        return "/index";

    }

    @GetMapping(value = "/main")
    public String main(HttpSession session, ModelMap model) throws Exception {

        log.info(this.getClass().getName() + ".main Start!");

        String userId = (String) session.getAttribute("SS_USER_ID");

        log.info("메인 띄우기 전에 세션에서 가져온 userId : " + userId);

        UserInfoDTO pDTO = UserInfoDTO.builder().userId(userId).build();

        UserInfoDTO iDTO = Optional.ofNullable(loginService.getIdentity(pDTO))
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

        log.info(this.getClass().getName() + ".main End!");

        return "/main";

    }

}
