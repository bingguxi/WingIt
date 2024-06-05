package kopo.poly.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MainController {

    @GetMapping(value = "/index")
    public String index() {

        log.info(this.getClass().getName() + ".index Start!");
        log.info(this.getClass().getName() + ".index End!");

        return "/index";

    }

    @GetMapping(value = "/main")
    public String main() throws Exception {

        log.info(this.getClass().getName() + ".main Start!");
        log.info(this.getClass().getName() + ".main End!");

        return "/main";

    }

    @GetMapping(value = "/adminMain")
    public String adminMain() throws Exception {

        log.info(this.getClass().getName() + ".adminMain Start!");
        log.info(this.getClass().getName() + ".adminMain End!");

        return "admin/adminMain";
    }

}
