package kopo.poly.service.impl;

import kopo.poly.dto.MailDTO;
import kopo.poly.service.IMailService;
import kopo.poly.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Slf4j
@RequiredArgsConstructor
@Service
public class MailService implements IMailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromMail;

    @Override
    public int doSendMail(MailDTO pDTO) {

        log.info(this.getClass().getName() + ".doSendMail Start!");

        int res = 1; // 메일 발송 성공여부

        if (pDTO == null) {
            pDTO = MailDTO.builder().build();
        }

        String toWho = CmmUtil.nvl(pDTO.toWho());
        String title = CmmUtil.nvl(pDTO.title());
        String contents = CmmUtil.nvl(pDTO.contents());

        log.info("메일 발송 정보");
        log.info("toWho : " + toWho);
        log.info("title : " + title);
        log.info("contents : " + contents);

        MimeMessage message = mailSender.createMimeMessage(); // 메일 발송 메시지 구조 (파일 첨부도 가능하대!)

        MimeMessageHelper messageHelper = new MimeMessageHelper(message, "UTF-8"); // 메일 발송 메시지 구조를 쉽게 생성하게 도와주는 객체

        try {

            messageHelper.setTo(toWho);
            messageHelper.setFrom(fromMail);
            messageHelper.setSubject(title);
            messageHelper.setText(contents);

            mailSender.send(message);

        } catch (Exception e) {
            res = 0;
            log.info("[ERROR] " + this.getClass().getName() + ".doSendMail : " + e);
        }

        log.info(this.getClass().getName() + ".doSendMail End!");

        return res;
    }
}
