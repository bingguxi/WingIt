package kopo.poly.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Builder(toBuilder = true)
@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class TodoDTO {

    String todoSeq; // 할 일 번호
    String userId; // 할 일 작성자
    String content; // 할 일
    String complete; // 할 일 완료 여부 (yes/no)

}
