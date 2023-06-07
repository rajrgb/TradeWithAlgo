package com.algo.trading.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.algo.trading.models.Credentials;

public interface CredentialsRepository extends JpaRepository<Credentials, Integer> {

}
