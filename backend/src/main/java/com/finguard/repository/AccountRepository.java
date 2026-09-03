package com.finguard.repository;
import com.finguard.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.*;
public interface AccountRepository extends JpaRepository<Account, UUID> { Optional<Account> findByAccountNumber(String number); Optional<Account> findByUserId(UUID userId); @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select a from Account a where a.id=:id") Optional<Account> findByIdForUpdate(@Param("id") UUID id); }
