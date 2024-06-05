package kopo.poly.controller;

import jakarta.validation.Valid;
import kopo.poly.controller.response.CommonResponse;
import kopo.poly.dto.ChatDTO;
import kopo.poly.dto.MsgDTO;
import kopo.poly.service.IMongoService;
import kopo.poly.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping(value = "/cs")
public class MongoController {

    private final IMongoService mongoService;

    /**
     * 고객센터 채팅창 접속
     */
    @GetMapping(value = "chatroom")
    public String chatroom() throws Exception {

        log.info(this.getClass().getName() + ".chatroom Start!");
        log.info(this.getClass().getName() + ".chatroom End!");

        return "/cs/chatroom";
    }

    @PostMapping(value = "saveChat")
    public ResponseEntity saveChat(@Valid @RequestBody ChatDTO pDTO, BindingResult bindingResult) throws Exception {

        log.info(this.getClass().getName() + ".saveChat Start!");

        if (bindingResult.hasErrors()) { // Spring Validation 맞춰 잘 바인딩 되었는지 체크
            return CommonResponse.getErrors(bindingResult); // 유효성 검증 결과에 따른 에러 메시지 전달

        }

        String msg = ""; // 저장 결과 메세지

        log.info("pDTO : " + pDTO); // 입력 받은 값 확인하기

        pDTO = pDTO.toBuilder().date(DateUtil.getDateTime("yyyy-MM-dd HH:mm:ss")).build();

        log.info("채팅 발송시간 추가하고 난 뒤의 pDTO : " + pDTO);

        mongoService.insertChat(pDTO);

        String res = "성공";

        log.info(this.getClass().getName() + ".saveChat End!");

        return ResponseEntity.ok(
                CommonResponse.of(HttpStatus.OK, HttpStatus.OK.series().name(), res)
        );

    }

    @PostMapping(value = "getChats")
    public ResponseEntity getChats(@RequestBody ChatDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".getChats Start!");

        log.info("pDTO : " + pDTO);

        List<ChatDTO> rList = Optional.ofNullable(mongoService.getChats(pDTO))
                        .orElseGet(ArrayList::new);

        log.info(this.getClass().getName() + ".getChats End!");

        return ResponseEntity.ok(
                CommonResponse.of(HttpStatus.OK, HttpStatus.OK.series().name(), rList)
        );

    }

}
