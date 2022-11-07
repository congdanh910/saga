package com.sg.ws.saga.paymentsservice.core.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<PaymentsEntity, String> {

}
