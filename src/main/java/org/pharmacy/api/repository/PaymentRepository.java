package org.pharmacy.api.repository;

import org.pharmacy.api.model.Payment;
import org.pharmacy.api.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrder(Order order);
    List<Payment> findByOrderUser_Id(Long userId);
}
