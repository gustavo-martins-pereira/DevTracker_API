package com.gustavomp.devtrackerapi.repositories;

import com.gustavomp.devtrackerapi.models.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {

    Optional<Client> findByName(String name);

}
