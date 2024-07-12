package kopo.poly.controller;

import jakarta.servlet.http.HttpSession;
import kopo.poly.chat.ChatHandler;
import kopo.poly.config.GlobalVariables;
import kopo.poly.util.CmmUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@Controller
@RequestMapping(value = "/studyWithMe")
@RequiredArgsConstructor
public class StudyWithMeController {

    private final GlobalVariables globalVariables;

    /**
     * 채팅창 입장 전
     */
    @GetMapping(value = "")
    public String studyWithMe() {

        log.info(this.getClass().getName() + ".studyWithMe Start!");

        log.info(this.getClass().getName() + ".studyWithMe Ends!");

        return "studyWithMe/intro";
    }

    @GetMapping("/roomCount/{roomName}")
    public ResponseEntity<Map<String, Integer>> getRoomCount(@PathVariable String roomName) {
        Integer roomCount = globalVariables.getCheckRoomIdCount().get(roomName);
        if (roomCount == null) {
            roomCount = 0; // 방이 없으면 0명으로 설정
        }
        Map<String, Integer> response = new HashMap<>();
        response.put("roomCount", roomCount);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 채팅창 접속
     */
    @PostMapping(value = "chatroom")
    public String chatroom(HttpServletRequest request, HttpSession session, ModelMap model) {

        log.info(this.getClass().getName() + ".chatroom Start!");

        String roomName = CmmUtil.nvl(request.getParameter("roomName"));
        String userName = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));

        log.info("roomName : " + roomName);
        log.info("userName : " + userName);

        model.addAttribute("roomName", roomName);
        model.addAttribute("userName", userName);

        log.info(this.getClass().getName() + ".chatroom End!");

        return "studyWithMe/chatroom";
    }

    /**
     * 채팅방 목록
     */
    @RequestMapping(value = "roomList")
    @ResponseBody
    public Set<String> roomList() {

        log.info(this.getClass().getName() + ".roomList Start!");

        log.info(this.getClass().getName() + ".roomList Ends!");

        return ChatHandler.roomInfo.keySet();

    }
}