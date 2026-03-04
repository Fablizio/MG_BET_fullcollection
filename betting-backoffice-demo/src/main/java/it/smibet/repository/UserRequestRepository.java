package it.smibet.repository;

import it.smibet.domain.UserRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRequestRepository extends JpaRepository<UserRequest, Integer> {


}
