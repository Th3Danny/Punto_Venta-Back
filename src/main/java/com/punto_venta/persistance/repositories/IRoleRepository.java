package com.punto_venta.persistance.repositories;

import com.punto_venta.persistance.entities.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface IRoleRepository extends JpaRepository<Roles, Long> {
    Optional<Roles> findByName(String name);
}
