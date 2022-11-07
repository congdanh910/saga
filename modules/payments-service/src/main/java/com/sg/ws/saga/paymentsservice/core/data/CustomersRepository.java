package com.sg.ws.saga.paymentsservice.core.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomersRepository extends JpaRepository<CustomersEntity, String> {

}
