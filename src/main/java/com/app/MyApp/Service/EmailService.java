package com.app.MyApp.Service;

import com.app.MyApp.Model.User;
import com.app.MyApp.Model.VerificationToken;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Value("${email.from}")
    private String fromAddress;

    @Value("${app.frontend.url}")
    private String url;

    @Autowired
    JavaMailSender javaMailSender;

    public Boolean sendVerificationEmail(VerificationToken token){

        String subject = "Email Verification";
        String verificationLink = "http://localhost:3000/verify?token=" + token.getToken();

        String emailContent = "<p>Please click the link below to verify your email address: </p>"
                + "<p><a href=\"" + verificationLink + "\">Verify Email</a></p>";

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

        try {
            helper.setFrom("no-reply@demo.com");
            helper.setTo(token.getUser().getEmail());
            helper.setSubject(subject);
            helper.setText(emailContent, true); // Set to 'true' to enable HTML content

            javaMailSender.send(mimeMessage);
            return true;
        } catch (MessagingException e) {
            return false;
        }
    }

    public Boolean sendPasswordResetMail(User user, String token){

        String subject = "Reset Password Email Verification";
        String verificationLink = "http://localhost:3000/updatepassword?token=" + token;

        String emailContent = "<p>Please click the link below to update password: </p>"
                + "<p><a href=\"" + verificationLink + "\">Verify Email</a></p>";

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

        try {
            helper.setFrom("no-reply@demo.com");
            helper.setTo(user.getEmail());
            helper.setSubject(subject);
            helper.setText(emailContent, true); // Set to 'true' to enable HTML content

            javaMailSender.send(mimeMessage);
            System.out.println("Mail Sent For Password Reset");
            return true;
        } catch (MessagingException e) {
            return false;
        }
    }

}
