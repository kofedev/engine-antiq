package dev.kofe.kengine.service.impl;

import dev.kofe.kengine.dto.UserDTO;
import dev.kofe.kengine.mail.EmailService;
import dev.kofe.kengine.mapper.UserMapper;
import dev.kofe.kengine.model.Role;
import dev.kofe.kengine.model.User;
import dev.kofe.kengine.repository.RoleRepository;
import dev.kofe.kengine.repository.UserRepository;
import dev.kofe.kengine.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.Random;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final EmailService emailService;

    @Value("${engine.base.server}")
    private String server;

    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder,
                           UserMapper userMapper,
                           EmailService emailService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.emailService = emailService;
    }

    @Override
    public User loadUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User createUser(String email, String password) {
        String encodedPassword = passwordEncoder.encode(password);
        return userRepository.save(new User(email, encodedPassword));
    }

    @Override
    public void sendConfirmationEmail (User user) {

        // generate token
        String tokenToConfirmNewEmail = generateRandomToken(50);

        // save
        user.setEmailToken(tokenToConfirmNewEmail);
        //userRepository.save(user);

        // email: forming a link to confirm new email
        String linkString = server + "/issue/newmail?email=" + user.getEmail() + "&code=" + tokenToConfirmNewEmail;

        // form email body
        String emailBody = """
                    Hello!
                    This is a confirmation letter.
                    Please follow the link to confirm an email:                         
                    """ + linkString;

        // try to send an email
        try {
            emailService.sendEmail(user.getEmail(), "Confirm email", emailBody);
        } catch (MessagingException e) {
            System.out.println("MessagingException: " + e);
        }
    }

    @Override
    public void assignRoleToUser(String email, String roleName) {
        User user = loadUserByEmail(email);
        Role role = roleRepository.findByName(roleName);
        user.assignRoleToUser(role);
    }

    @Override
    public UserDTO changePassword(UserDTO userDTO) {
        User userUpdated = userRepository.findByEmail(userDTO.getEmail());
        if (userUpdated != null) {
            String encodedPassword = passwordEncoder.encode(userDTO.getPassword());
            userUpdated.setPassword(encodedPassword);
            userRepository.save(userUpdated);
            return userMapper.fromUser(userUpdated);
        } else {
            return null;
        }
    }

    @Override
    public UserDTO changeEmail(UserDTO userDTO) {
        User userUpdated = userRepository.findByEmail(userDTO.getEmail());
        String newEmail = userDTO.getPassword();
        if (userUpdated != null) {
            userUpdated.setEmail(newEmail);
            userUpdated.setConfirmed(false);

            // generate token
            String tokenToConfirmNewEmail = generateRandomToken(50);

            // save
            userUpdated.setEmailToken(tokenToConfirmNewEmail);
            userRepository.save(userUpdated);

            // email: forming a link to confirm new email
            String linkString = server + "/issue/newmail?email=" + newEmail + "&code=" + tokenToConfirmNewEmail;

            // form email body
            String emailBody = """
                    Hello!
                    This is a confirmation letter.
                    Please follow the link to confirm an email:                         
                    """ + linkString;

            // try to send an email
            try {
                emailService.sendEmail(userUpdated.getEmail(), "Confirm email", emailBody);
            } catch (MessagingException e) {
                System.out.println("MessagingException: " + e);
            }

            return userMapper.fromUser(userUpdated);
        } else {
            return null;
        }
    }

    static String generateRandomToken(int length) {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[length];
        random.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        return token;
    }

    // ATTENTION: NO MULTIPLY ROLE VERSION :: SINGLE ROLE VERSION!
    @Override
    public UserDTO changeSingleRole(UserDTO userDTO) {
        User userToUpdate = userRepository.findByEmail(userDTO.getEmail());
        if (userToUpdate != null) {
            Role adminRole = roleRepository.findByName("Admin");
            Role staffRole = roleRepository.findByName("Staff");
            if (userToUpdate.getRoles().contains(adminRole)) {
                userToUpdate.getRoles().remove(adminRole);
                userToUpdate.assignRoleToUser(staffRole);
            } else {
                if (userToUpdate.getRoles().contains(staffRole)) {
                    userToUpdate.getRoles().remove(staffRole);
                    userToUpdate.assignRoleToUser(adminRole);
                }
            }
            userRepository.save(userToUpdate);
            return userMapper.fromUser(userToUpdate);
        } else {
            return null;
        }
    }

}
