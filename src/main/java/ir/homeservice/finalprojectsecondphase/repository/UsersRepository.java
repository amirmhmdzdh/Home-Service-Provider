package ir.homeservice.finalprojectsecondphase.repository;

import ir.homeservice.finalprojectsecondphase.model.user.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {

    Optional<Users> findByEmail(String email);




}
