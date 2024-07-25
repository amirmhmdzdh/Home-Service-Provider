package ir.homeservice.finalprojectsecondphase.service;

import ir.homeservice.finalprojectsecondphase.exception.NotFoundException;
import ir.homeservice.finalprojectsecondphase.model.user.ConfirmationToken;
import ir.homeservice.finalprojectsecondphase.model.user.Users;
import ir.homeservice.finalprojectsecondphase.repository.ConfirmationTokenRepository;
import ir.homeservice.finalprojectsecondphase.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final UsersService usersService;
    private final JavaMailSender javaMailSender;
    private final UsersRepository usersRepository;
    private final ConfirmationTokenRepository confirmationTokenRepository;

    @Async
    public void createEmail(SimpleMailMessage email) {
        javaMailSender.send(email);
    }

    public void createEmail(String emailAddress) {
        Optional<Users> users = usersService.findByEmail(emailAddress);

        ConfirmationToken confirmationToken = new ConfirmationToken(users.get(), true);

        confirmationTokenRepository.save(confirmationToken);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("mktbsharif@gmail.com");
        mailMessage.setTo(emailAddress);
        mailMessage.setSubject("Complete Registration!");
        mailMessage.setText("Hi " + users.get().getLastName() + "\n To confirm your account, please click here : "
                + "http://localhost:8080/confirm-account?token=" + confirmationToken.getConfirmationToken());
        createEmail(mailMessage);
    }

    public void confirmEmail(String confirmationToken) {

        ConfirmationToken token = confirmationTokenRepository.findByConfirmationToken(confirmationToken);

        confirmEmailValidation(token);

        token.setActive(false);
        confirmationTokenRepository.save(token);

        Optional<Users> users = usersService.findByEmail(token.getUsers().getEmail());
        users.get().setIsActive(true);

        usersRepository.save(users.get());
    }

    private static void confirmEmailValidation(ConfirmationToken token) {
        if (token == null)
            throw new NotFoundException("EMAIL NOT CONFIRMED");

        if (!token.isActive())
            throw new NotFoundException("THIS TOKEN IS ALREADY USED");
    }


}
