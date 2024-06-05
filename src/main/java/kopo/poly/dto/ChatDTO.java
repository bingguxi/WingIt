package kopo.poly.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Builder(toBuilder = true)
public record ChatDTO (

        String name, // 이름
        String sender, // 채팅 발송자
        String msg, // 채팅 메시지
        String date // 발송날짜
//        String koMsg, //한국어 채팅 메시지
//        String enMsg // 영어 채팅 메시지

) {
}
