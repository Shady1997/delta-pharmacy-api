/*
 * Author: Shady Ahmed
 * Date: 2025-09-27
 * Project: Delta Pharmacy API
 */
package org.pharmacy.api.service;

import lombok.RequiredArgsConstructor;
import org.pharmacy.api.dto.ChatMessageRequest;
import org.pharmacy.api.dto.ChatMessageResponse;
import org.pharmacy.api.model.ChatMessage;
import org.pharmacy.api.model.User;
import org.pharmacy.api.repository.ChatMessageRepository;
import org.pharmacy.api.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Transactional
    public ChatMessageResponse sendMessage(ChatMessageRequest request, String senderEmail) {
        User sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        User receiver = userRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        ChatMessage message = new ChatMessage();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setMessage(request.getMessage());
        message.setIsRead(false);

        message = chatMessageRepository.save(message);

        // Send notification to receiver
        notificationService.createNotification(
                receiver,
                "New Message from " + sender.getFullName(),
                request.getMessage().length() > 50
                        ? request.getMessage().substring(0, 50) + "..."
                        : request.getMessage(),
                "CHAT_MESSAGE"
        );

        return mapToResponse(message);
    }

    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getConversation(Long userId1, Long userId2) {
        List<ChatMessage> messages = chatMessageRepository.findConversation(userId1, userId2);
        return messages.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void markAsRead(Long messageId) {
        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        message.setIsRead(true);
        chatMessageRepository.save(message);
    }

    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getUnreadMessages(Long userId) {
        List<ChatMessage> messages = chatMessageRepository.findUnreadMessages(userId);
        return messages.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<User> getUserConversations(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Get all messages involving this user
        List<ChatMessage> messages = chatMessageRepository.findUserMessages(user.getId());

        // Extract unique users (excluding current user)
        Set<User> conversationUsers = new LinkedHashSet<>();
        for (ChatMessage message : messages) {
            if (!message.getSender().getId().equals(user.getId())) {
                conversationUsers.add(message.getSender());
            }
            if (!message.getReceiver().getId().equals(user.getId())) {
                conversationUsers.add(message.getReceiver());
            }
        }

        return new ArrayList<>(conversationUsers);
    }

    @Transactional(readOnly = true)
    public User findPharmacistOrAdmin() {
        // Find first available pharmacist or admin
        return userRepository.findAll().stream()
                .filter(u -> u.getRole() == User.UserRole.PHARMACIST || u.getRole() == User.UserRole.ADMIN)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No pharmacist or admin available"));
    }

    private ChatMessageResponse mapToResponse(ChatMessage message) {
        ChatMessageResponse response = new ChatMessageResponse();
        response.setId(message.getId());
        response.setSenderId(message.getSender().getId());
        response.setSenderName(message.getSender().getFullName());
        response.setReceiverId(message.getReceiver().getId());
        response.setReceiverName(message.getReceiver().getFullName());
        response.setMessage(message.getMessage());
        response.setIsRead(message.getIsRead());
        response.setCreatedAt(message.getCreatedAt());
        return response;
    }
}