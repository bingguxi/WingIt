package kopo.poly.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Builder(toBuilder = true)
@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class TestDTO {

    int questionSeq; // 질문번호
    String question; // 질문
    String answer01; // 답변1
    String answer02; // 답변2
    String answer03; // 답변3
    String answer04; // 답변4
    String answer05; // 답변5
    String answer01full; // 답변1 풀버전 (TEST_GCG_ADULT 직업가치관검사 일반,대학생에서만 사용)
    String answer02full; // 답변2 풀버전 (TEST_GCG_ADULT 직업가치관검사 일반,대학생에서만 사용)
    String userId; // 검사 결과 url 저장을 위한 회원 아이디
    String qestrnSeq; // 심리 검사 번호
    String url; // 검사 결과 url

}
