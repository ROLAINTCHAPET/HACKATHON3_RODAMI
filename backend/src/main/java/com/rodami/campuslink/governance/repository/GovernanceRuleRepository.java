package com.rodami.campuslink.governance.repository;

import com.rodami.campuslink.governance.entity.GovernanceRule;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface GovernanceRuleRepository extends ReactiveCrudRepository<GovernanceRule, Long> {
    Mono<GovernanceRule> findByRuleKey(String ruleKey);
}
