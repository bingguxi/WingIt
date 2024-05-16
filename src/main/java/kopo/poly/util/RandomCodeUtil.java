package kopo.poly.util;

import java.util.Random;

public class RandomCodeUtil {
    //인증코드 만들기
    public static String createKey() {
        StringBuffer key = new StringBuffer();
        Random rnd = new Random();

        for (int i = 0; i < 6; i++) { //인증코드 6자리
            int index = rnd.nextInt(3); // 0~2까지 랜덤

            switch (index){
                case 0:
                    key.append((char) ((int) (rnd.nextInt(26)) + 97));
                    // a~z (ex. 1+98=98 => (char)98 = 'b')
                    break;
                case 1:
                    key.append((char) ((int) (rnd.nextInt(26)) + 65));
                    // A~Z
                    break;
                case 2:
                    key.append((rnd.nextInt(10)));
                    //0~9
                    break;
            }
        }

        return key.toString();
    }
}