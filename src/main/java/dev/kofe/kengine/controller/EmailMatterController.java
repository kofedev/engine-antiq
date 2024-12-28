package dev.kofe.kengine.controller;

import dev.kofe.kengine.mail.EmailService;
import dev.kofe.kengine.model.User;
import dev.kofe.kengine.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.mail.MessagingException;
import java.security.SecureRandom;
import java.util.Base64;

@Controller
@CrossOrigin("*")
@RequestMapping("/issue")
public class EmailMatterController {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Value("${engine.base.server}")
    private String server;

    @Value("${engine.base.adminpanel}")
    private String adminPanelUrl;

    public EmailMatterController(UserRepository userRepository,
                                 EmailService emailService,
                                 PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/newmail")
    public String confirmEmail (@RequestParam(name="email") String email, @RequestParam(name="code") String code, Model model) {

        String message = "";
        if (email != null && code != null) {
            User user = userRepository.findByEmail(email);
            if (user != null) {
                if (user.getConfirmed()) {
                    message = "This email is already confirmed.";
                } else {
                    if (user.getEmailToken().equals(code)) {
                        user.setConfirmed(true);
                        user.setEmailToken("");
                        userRepository.save(user);
                        message = "Thank you! Your email is confirmed!";
                    } else {
                        message = "Sorry. Something went wrong.";
                    }
                }
            } else {
                message = "Sorry. Something went wrong: email is not correct.";
            }
        }

        model.addAttribute("message", message);
        model.addAttribute("urlAdminPanel", adminPanelUrl);
        return "email-confirmed";
    }


    @AllArgsConstructor
    @Getter
    @Setter
    class RestoreResponse {
        String message = "";
    }

    @GetMapping("/restore")
    public ResponseEntity<RestoreResponse> sendEmailToRestorePassword (@RequestParam(name="email") String email) {
        String message = "";
        if (email == null) {
            message = "Sorry. Something went wrong: email required.";
        } else {
            User userToSetTemporaryPassword = userRepository.findByEmail(email);
            if (userToSetTemporaryPassword == null) {
                message = "Sorry. Something went wrong: user " + email + " is not found.";
            } else {

                // prepare an email
                // generate token
                String tokenToRestorePassword = generateRandomToken(50);

                // save
                userToSetTemporaryPassword.setEmailToken(tokenToRestorePassword);
                userRepository.save(userToSetTemporaryPassword);

                // email: forming a link to restore password
                String linkString = server +
                        "/issue/restoreconfirmation?email=" +
                        userToSetTemporaryPassword.getEmail() +
                        "&code=" + tokenToRestorePassword;

                // form email body
                String emailBody = """
                    Hello!
                    Somebody has asked to restore the password to the Kalba Engine admin panel.
                    If you are really want to restore the password, please follow the link to confirm this action:                         
                    """ + linkString;

                // try to send an email
                try {
                    emailService.sendEmail(userToSetTemporaryPassword.getEmail(), "Email about the password restore process", emailBody);
                } catch (MessagingException e) {
                    System.out.println("MessagingException: " + e);
                }

                message = "Email with the instructions has been sent to " + userToSetTemporaryPassword.getEmail() +
                        ". Check your mail and follow the instructions.";

            }
        }

        return new ResponseEntity<>(new RestoreResponse(message), HttpStatus.OK);
    }


    @GetMapping("/restoreconfirmation")
    public String generateTemporaryPassword (@RequestParam(name="email") String email, @RequestParam(name="code") String code, Model model) {
        String message = "";
        if (email == null || code == null) {
            message = "Sorry. Something went wrong: email is not correct.";
        } else {
            User user = userRepository.findByEmail(email);
            if (user == null) {
                message = "User " + email + " not found.";
            } else {
                if (user.getEmailToken() == null) {
                    message = "Sorry. Something went wrong.";
                } else {
                    if (user.getEmailToken().equals(code)) {
                        // OK
                        user.setEmailToken("");
                        // generate a temporary password
                        String temporaryPassword = generateRandomToken(10);
                        String encodedTemporaryPassword = passwordEncoder.encode(temporaryPassword);
                        // save
                        user.setPassword(encodedTemporaryPassword);
                        userRepository.save(user);
                        // form the message with the temporary password
                        String messageWithTemporaryPassword = "Use the temporary password:\n\n"
                        + temporaryPassword + "\n\n" +
                        "to login to the system." +
                        "We strongly recommend changing the password to your own immediately after login.";
                        // form email body
                        String emailBody = messageWithTemporaryPassword;
                        // try to send an email
                        try {
                            emailService.sendEmail(user.getEmail(), "Password restore", emailBody);
                        } catch (MessagingException e) {
                            System.out.println("MessagingException: " + e);
                        }
                        // message
                        message = "Email with the instructions has been sent to " + user.getEmail() +
                                ". Check your mail and follow the instructions.";
                    } else {
                        message = "Sorry. Something went wrong.";
                    }
                }
            }
        }

        model.addAttribute("message", message);
        model.addAttribute("urlAdminPanel", adminPanelUrl);
        return "email-confirmed";
   }


    private String generateRandomToken(int length) {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[length];
        random.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        return token;
    }


}
