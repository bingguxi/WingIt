package kopo.poly.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Builder(toBuilder = true)
@Getter
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class UserInfoDTO {

    String userId; // 회원아이디
    String userName; // 회원이름
    String nickname; // 닉네임
    String password; // 비밀번호
    String identity; // 직업 (고등학생, 대학생, 일반)
    String gender; // 성별
    String email; // 이메일
    String tel; // 전화번호 (소셜로그인)
    String birth; // 생년월일 (소셜로그인)
//    String gender; // 성별 (소셜로그인)
    String regId; // 등록자아이디
    String regDt; // 등록일시
    String chgId; // 최근 수정자아이디
    String chgDt; // 최근 수정일시
    String existsYn; // 회원아이디 존재여부
    int authNumber; // 인증번호

}
