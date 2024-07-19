package ir.homeservice.finalprojectsecondphase.service;

import ir.homeservice.finalprojectsecondphase.exception.NotFoundException;
import ir.homeservice.finalprojectsecondphase.model.user.Users;
import ir.homeservice.finalprojectsecondphase.repository.UsersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UsersService {

    private final UsersRepository usersRepository;

    public Optional<Users> findByEmail(String email) {
        return Optional.ofNullable(usersRepository.findByEmail(email).orElseThrow(
                () -> new NotFoundException(
                        String.format("USER %s NOT FOUND", email)
                )
        ));
    }
}


