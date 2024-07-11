package ir.homeservice.finalprojectsecondphase.repository;

import ir.homeservice.finalprojectsecondphase.model.user.Users;
import ir.homeservice.finalprojectsecondphase.model.user.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {

    Optional<Users> findByEmail(String email);

    @Query("select u from Users u where u.role =:role ")
    List<Users> findAllByRole(Role role);

    Optional<Users> findByEmailAndPassword(String email, String password);


}
