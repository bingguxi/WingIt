package kopo.poly.service;

import kopo.poly.dto.TodoDTO;

import java.util.List;

public interface ITodoService {

    List<TodoDTO> getTodoList(TodoDTO pDTO) throws Exception; // 할 일 목록 조회하기

    int insertTodo(TodoDTO pDTO) throws Exception; // 할 일 추가

    void completeTodo(TodoDTO pDTO) throws Exception; // 할 일 완료 / 완료해제

    void deleteTodo(TodoDTO pDTO) throws Exception; // 할 일 삭제

}
