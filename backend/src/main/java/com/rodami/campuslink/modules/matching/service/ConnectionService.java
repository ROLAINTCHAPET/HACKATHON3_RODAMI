package com.rodami.campuslink.modules.matching.service;

import com.rodami.campuslink.common.exception.ResourceNotFoundException;
import com.rodami.campuslink.profile.entity.Connection;
import com.rodami.campuslink.modules.matching.dto.ConnectionRequest;
import com.rodami.campuslink.modules.matching.dto.ConnectionResponse;
import com.rodami.campuslink.profile.repository.ConnectionRepository;
import com.rodami.campuslink.profile.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConnectionService {

    private final ConnectionRepository connectionRepository;
    private final UserRepository userRepository;

    // ----------------------------------------------------------------
    // Créer une demande de connexion
    // ----------------------------------------------------------------
    @Transactional
    public Mono<ConnectionResponse> createConnection(Long requesterId, ConnectionRequest request) {
        Long targetId = request.getTargetUserId();

        if (requesterId.equals(targetId)) {
            return Mono.error(new IllegalArgumentException("Impossible de se connecter à soi-même"));
        }

        return userRepository.findById(targetId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Utilisateur cible", targetId)))
                .then(connectionRepository.existsBetweenUsers(requesterId, targetId))
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new IllegalArgumentException("Une connexion existe déjà entre ces utilisateurs"));
                    }
                    Connection connection = Connection.builder()
                            .requesterId(requesterId)
                            .receiverId(targetId)
                            .status("PENDING")
                            .sourceEventId(request.getSourceEventId())
                            .build();
                    return connectionRepository.save(connection);
                })
                .map(this::toResponse);
    }

    // ----------------------------------------------------------------
    // Accepter ou refuser une connexion
    // ----------------------------------------------------------------
    @Transactional
    public Mono<ConnectionResponse> updateStatus(Long connectionId, Long userId, String newStatus) {
        return connectionRepository.findById(connectionId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Connexion", connectionId)))
                .flatMap(conn -> {
                    // Seul le destinataire peut accepter/refuser
                    if (!conn.getReceiverId().equals(userId)) {
                        return Mono.error(new IllegalArgumentException(
                                "Seul le destinataire peut modifier le statut de cette connexion"));
                    }
                    if (!"ACCEPTED".equals(newStatus) && !"BLOCKED".equals(newStatus)) {
                        return Mono.error(new IllegalArgumentException(
                                "Statut invalide. Valeurs autorisées : ACCEPTED, BLOCKED"));
                    }
                    conn.setStatus(newStatus);
                    return connectionRepository.save(conn);
                })
                .map(this::toResponse);
    }

    // ----------------------------------------------------------------
    // Supprimer (retirer) une connexion
    // ----------------------------------------------------------------
    @Transactional
    public Mono<Void> deleteConnection(Long connectionId, Long userId) {
        return connectionRepository.findById(connectionId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Connexion", connectionId)))
                .flatMap(conn -> {
                    if (!conn.getRequesterId().equals(userId) && !conn.getReceiverId().equals(userId)) {
                        return Mono.error(new IllegalArgumentException(
                                "Vous n'êtes pas autorisé à supprimer cette connexion"));
                    }
                    return connectionRepository.delete(conn);
                });
    }

    // ----------------------------------------------------------------
    // Lecture
    // ----------------------------------------------------------------
    public Flux<ConnectionResponse> getConnections(Long userId) {
        return connectionRepository.findAcceptedConnectionsByUserId(userId)
                .map(this::toResponse);
    }

    public Flux<ConnectionResponse> getPendingRequests(Long userId) {
        return connectionRepository.findPendingRequestsForUser(userId)
                .map(this::toResponse);
    }

    // ----------------------------------------------------------------
    // Mapping domain → DTO
    // ----------------------------------------------------------------
    private ConnectionResponse toResponse(Connection c) {
        return ConnectionResponse.builder()
                .id(c.getId())
                .userId1(c.getRequesterId())
                .userId2(c.getReceiverId())
                .status(c.getStatus())
                .sourceEventId(c.getSourceEventId())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }
}
