package kopo.poly.service.impl;

import kopo.poly.dto.TodoDTO;
import kopo.poly.persistance.mapper.ITodoMapper;
import kopo.poly.service.ITodoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class TodoService implements ITodoService {

    private final ITodoMapper todoMapper;

    /**
     * 사용자별 할 일 목록 조회하기
     * @param pDTO
     * @return
     * @throws Exception
     */
    @Override
    public List<TodoDTO> getTodoList(TodoDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".getTodoList Start!");

        log.info("컨트롤러에서 파라미터로 넘어온 userId : " + pDTO.getUserId());

        List<TodoDTO> rList = Optional.ofNullable(todoMapper.getTodoList(pDTO))
                        .orElseGet(ArrayList::new);

        log.info(this.getClass().getName() + ".getTodoList End!");

        return rList;
    }

    @Transactional
    @Override
    public int insertTodo(TodoDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".insertTodo Start!");

        int res = 0;

        log.info("컨트롤러에서 파라미터로 넘어온 userId : " + pDTO.getUserId());
        log.info("컨트롤러에서 파라미터로 넘어온 content : " + pDTO.getContent());

        String complete = "no";

        pDTO.toBuilder().complete(complete).build();

        res = todoMapper.insertTodo(pDTO);

        log.info(this.getClass().getName() + ".insertTodo End!");

        return res;
    }

    @Transactional
    @Override
    public void completeTodo(TodoDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".completeTodo Start!");

        log.info("컨트롤러에서 파라미터로 넘어온 userId : " + pDTO.getUserId());
        log.info("컨트롤러에서 파라미터로 넘어온 todoSeq : " + pDTO.getTodoSeq());
        log.info("컨트롤러에서 파라미터로 넘어온 complete : " + pDTO.getComplete());

        String complete = null;

        if (pDTO.getComplete().equals("yes")) {

            complete = "no";

        } else if (pDTO.getComplete().equals("no")) {

            complete = "yes";

        }

        // TODO 변경된 complete 값이 들어가는지 확인!
        // TODO 안 들어가면 새로운 DTO 만들어서 값 3개(usereId, todoSeq, 변경된 complete) 넣어주기
        pDTO.toBuilder().complete(complete).build();
        log.info("변경된 complete 값으로 pDTO가 빌드되었는지 확인 : " + pDTO.toString());

        todoMapper.completeTodo(pDTO);

        log.info(this.getClass().getName() + ".completeTodo End!");

    }

    @Transactional
    @Override
    public void deleteTodo(TodoDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".deleteTodo Start!");

        log.info("컨트롤러에서 파라미터로 넘어온 userId : " + pDTO.getUserId());
        log.info("컨트롤러에서 파라미터로 넘어온 todoSeq : " + pDTO.getTodoSeq());

        todoMapper.deleteTodo(pDTO);

        log.info(this.getClass().getName() + ".deleteTodo End!");

    }
}
