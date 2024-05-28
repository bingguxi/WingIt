package kopo.poly.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kopo.poly.dto.MsgDTO;
import kopo.poly.dto.TodoDTO;
import kopo.poly.service.ITodoService;
import kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "todo")
@Controller
public class TodoController {

    private final ITodoService todoService;

    @ResponseBody
    @PostMapping(value = "getTodoList")
    public List<TodoDTO> getTodoList(HttpSession session) throws Exception {

        log.info(this.getClass().getName() + ".getTodoList Start!");

        String userId = (String) session.getAttribute("SS_USER_ID");
        log.info("세션에서 가져온 userId : " + userId);

        TodoDTO pDTO = TodoDTO.builder().userId(userId).build();

        List<TodoDTO> rList = Optional.ofNullable(todoService.getTodoList(pDTO))
                        .orElseGet(ArrayList::new);

        log.info(this.getClass().getName() + ".getTodoList End!");

        return rList;

    }

    @ResponseBody
    @PostMapping(value = "insertTodo")
    public void insertTodo(HttpSession session, HttpServletRequest request) throws Exception {

        log.info(this.getClass().getName() + ".insertTodo Start!");

        String userId = (String) session.getAttribute("SS_USER_ID");
        String content = CmmUtil.nvl(request.getParameter("content"));

        log.info("세션에서 가져온 userId : " + userId);
        log.info("content : " + content);

        TodoDTO pDTO = TodoDTO.builder().userId(userId).content(content).build();

        todoService.insertTodo(pDTO);

        log.info(this.getClass().getName() + ".insertTodo End!");

    }

    @ResponseBody
    @PostMapping(value = "completeTodo")
    public void completeTodo(HttpSession session, HttpServletRequest request) throws Exception {

        log.info(this.getClass().getName() + ".completeTodo Start!");

        String userId = (String) session.getAttribute("SS_USER_ID");
        String todoSeq = CmmUtil.nvl(request.getParameter("todoSeq"));
        String complete = CmmUtil.nvl(request.getParameter("complete"));

        log.info("세션에서 가져온 userId : " + userId);
        log.info("todoSeq : " + todoSeq );
        log.info("complete : " + complete );

        TodoDTO pDTO = TodoDTO
                .builder()
                .userId(userId)
                .todoSeq(todoSeq)
                .complete(complete)
                .build();

        todoService.completeTodo(pDTO);

        log.info(this.getClass().getName() + ".completeTodo End!");

    }

    @ResponseBody
    @PostMapping(value = "deleteTodo")
    public void deleteTodo(HttpSession session, HttpServletRequest request) throws Exception {

        log.info(this.getClass().getName() + ".deleteTodo Start!");

        String userId = (String) session.getAttribute("SS_USER_ID");
        String todoSeq = CmmUtil.nvl(request.getParameter("todoSeq"));

        log.info("세션에서 가져온 userId : " + userId);
        log.info("todoSeq : " + todoSeq );

        TodoDTO pDTO = TodoDTO.builder().userId(userId).todoSeq(todoSeq).build();

        todoService.deleteTodo(pDTO);

        log.info(this.getClass().getName() + ".deleteTodo End!");

    }

}
