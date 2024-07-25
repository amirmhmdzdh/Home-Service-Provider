package ir.homeservice.finalprojectsecondphase.service;

import ir.homeservice.finalprojectsecondphase.exception.NotFoundException;
import ir.homeservice.finalprojectsecondphase.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class CustomerUserDetailsService implements UserDetailsService {

    private final static String USER_NOT_FOUND_MSG =
            "user with email %s not found";
    @Autowired
    private UsersRepository usersRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {
        return usersRepository.findByEmail(email)
                .orElseThrow(() ->
                        new NotFoundException(
                                String.format(USER_NOT_FOUND_MSG, email)));
    }
}

