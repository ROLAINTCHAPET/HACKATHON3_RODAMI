package com.rodami.campuslink.governance.repository;

import com.rodami.campuslink.governance.entity.AuditLog;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface AuditLogRepository extends ReactiveCrudRepository<AuditLog, Long> {
    Flux<AuditLog> findByEntityTypeAndEntityId(String entityType, Long entityId);
}
