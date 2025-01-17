package net.datasa.ruruplan.member.service;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import net.datasa.ruruplan.member.repository.MemberRepository;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    private final MemberRepository memberRepository;
    //인증 번호
    private String authNum;


    /**
     * 이메일 즁복 검사 서비스
     * @param email 입력받은 메일
     * @return      중복 이메일 중복 여부
     */
    public boolean emailDuplicate(String email) {
        return memberRepository.findByEmail(email).isPresent();
    }

    /**
     * 전송 메세지 생성 서비스
     * @param to
     * @return
     * @throws MessagingException
     * @throws UnsupportedEncodingException
     */
    public MimeMessage createMessage(String to)throws MessagingException, UnsupportedEncodingException {

        MimeMessage message = javaMailSender.createMimeMessage();

        message.addRecipients(Message.RecipientType.TO, to);	//보내는 대상
        message.setSubject("회원가입 이메일 인증");		//제목

        String msgg = "";
        msgg += "<div style='margin:100px;'>";
        msgg += "<h1> 안녕하세요</h1>";
        msgg += "<br>";
        msgg += "<p>아래 코드를 회원가입 창으로 돌아가 입력해주세요<p>";
        msgg += "<br>";
        msgg += "<div align='center' style='border:1px solid black; font-family:verdana';>";
        msgg += "<h3 style='color:blue;'>회원가입 인증 코드입니다.</h3>";
        msgg += "<div style='font-size:130%'>";
        msgg += "CODE : <strong>";
        msgg += authNum + "</strong>";	//메일 인증번호
        msgg += "</div>";
        message.setText(msgg, "utf-8", "html");
        message.setFrom(new InternetAddress("jkdev12345@gmail.com", "BulletBox"));

        //해당 메시지는 아래에 예시

        return message;
    }

    /**
     * 인증 코드 생성 서비스
     * @return 인증 코드
     */
    public String createCode(){
        Random random = new Random();
        StringBuffer key = new StringBuffer();

        for(int i = 0; i< 8; i++){	//인증 코드 8자리
            int index = random.nextInt(3);	//0~2까지 랜덤, 랜덤값으로 switch문 실행

            switch (index) {
                case 0 -> key.append((char) ((int) random.nextInt(26) + 97));
                case 1 -> key.append((char) (int) random.nextInt(26) + 65);
                case 2 -> key.append(random.nextInt(9));
            }
        }
        return authNum = key.toString();
    }

    /**
     * 이메일 전송 서비스
     * @param sendEmail
     * @return 인증번호
     * @throws Exception
     */
    public String sendSimpleMessage(String sendEmail) throws Exception{

        authNum = createCode();	//랜덤 인증번호 생성

        MimeMessage message = createMessage(sendEmail);	//메일 발송
        try{
            javaMailSender.send(message);
        }catch (MailException es){
            es.printStackTrace();
            throw new IllegalArgumentException();
        }

        return authNum;
    }

}
