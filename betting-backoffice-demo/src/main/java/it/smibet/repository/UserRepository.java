package it.smibet.repository;

import it.smibet.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    List<User> findAllByOrderByExpirationAscNicknameAsc();

    Optional<User> findByCode(String code);

    Optional<User> findByTelegramSession(String telegram);

    Boolean existsByCode(String code);

    Boolean existsByCodeAndIdNot(String code, Integer id);

    @Query(value = "SELECT DISTINCT(U.telegramSession) FROM User U WHERE U.telegramSession IS NOT NULL")
    List<String> getTelegramSession();

    @Query(value = "SELECT DISTINCT(U.telegramSession) FROM User U WHERE U.telegramSession IS NOT NULL and U.expiration > CURRENT_DATE")
    List<String> getTelegramSessionActive();

    boolean existsByFriendCode(String friendCode);
}
