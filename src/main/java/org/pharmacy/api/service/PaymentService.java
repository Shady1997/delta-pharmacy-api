package org.pharmacy.api.service;

import org.pharmacy.api.dto.PaymentRequest;
import org.pharmacy.api.dto.PaymentVerificationRequest;
import org.pharmacy.api.model.Order;
import org.pharmacy.api.model.Payment;
import org.pharmacy.api.repository.OrderRepository;
import org.pharmacy.api.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final NotificationService notificationService;

    @Transactional
    public Payment initiatePayment(PaymentRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setStatus(Payment.PaymentStatus.PENDING);
        payment.setTransactionId("TXN-" + UUID.randomUUID().toString());

        payment = paymentRepository.save(payment);

        notificationService.createNotification(
                order.getUser(),
                "Payment Initiated",
                "Payment of $" + payment.getAmount() + " has been initiated for order #" + order.getId(),
                "PAYMENT_UPDATE"
        );

        return payment;
    }

    @Transactional
    public Payment verifyPayment(PaymentVerificationRequest request) {
        Payment payment = paymentRepository.findById(request.getPaymentId())
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        // Mock payment verification - in real scenario, this would call payment gateway
        boolean isVerified = mockPaymentGatewayVerification(request.getTransactionId());

        if (isVerified) {
            payment.setStatus(Payment.PaymentStatus.COMPLETED);
            payment.setCompletedAt(LocalDateTime.now());
            payment.setTransactionId(request.getTransactionId());

            // Update order status
            Order order = payment.getOrder();
            order.setStatus(Order.OrderStatus.CONFIRMED);
            orderRepository.save(order);

            notificationService.createNotification(
                    order.getUser(),
                    "Payment Successful",
                    "Payment completed for order #" + order.getId(),
                    "PAYMENT_UPDATE"
            );
        } else {
            payment.setStatus(Payment.PaymentStatus.FAILED);

            notificationService.createNotification(
                    payment.getOrder().getUser(),
                    "Payment Failed",
                    "Payment failed for order #" + payment.getOrder().getId(),
                    "PAYMENT_UPDATE"
            );
        }

        return paymentRepository.save(payment);
    }

    private boolean mockPaymentGatewayVerification(String transactionId) {
        // Mock verification - always returns true
        return transactionId != null && !transactionId.isEmpty();
    }

    public List<Payment> getPaymentHistory(Long userId) {
        return paymentRepository.findByOrderUser_Id(userId);
    }

    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
    }
}

