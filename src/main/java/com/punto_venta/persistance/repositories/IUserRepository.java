package com.punto_venta.persistance.repositories;

import com.punto_venta.persistance.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IUserRepository extends JpaRepository<Users, Long> {
    @Override
    Optional<Users> findById(Long aLong);

    Optional<Users> findByEmail(String email);

    Optional<Users> findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);


    @Query("select u from Users u left join fetch u.roles where u.email = :email")
    Optional<Users> findByEmailFetchRoles(@Param("email") String email);

    @Query(value = "SELECT r.role_id FROM user_role r JOIN user u ON u.id = r.user_id WHERE u.email = :email", nativeQuery = true)
    java.util.List<Long> findRoleIdsByUserEmail(@Param("email") String email);
}


