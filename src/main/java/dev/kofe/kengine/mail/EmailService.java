package dev.kofe.kengine.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired private JavaMailSender javaMailSender;

    public void sendEmail(String to, String subject, String text) throws MailException, MessagingException {

        try {

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("enginesender@kalba.co");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true);

            javaMailSender.send(message);

        } catch (MailException mailException) {
            System.out.println("Failed to send email: " + mailException.getMessage());
        } catch (MessagingException msgException) {
            System.out.println("Failed to send email: " + msgException.getMessage());
        }

    }

}
