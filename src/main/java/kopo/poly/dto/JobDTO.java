package kopo.poly.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Builder(toBuilder = true)
@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class JobDTO {

    String jobName; // 직업명
    String summary; // 하는일
    String aptitude; // 핵심능력
    String major; // 관련학과
    String capacity; // 관련자격증
    String certification; // 자격취득방법
    String possibility; // 직업전망
    String jobdicSeq;

}
