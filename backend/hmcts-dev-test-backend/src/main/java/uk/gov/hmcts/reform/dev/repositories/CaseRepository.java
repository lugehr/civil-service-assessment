package uk.gov.hmcts.reform.dev.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import uk.gov.hmcts.reform.dev.models.Case;

public interface CaseRepository extends JpaRepository<Case, Integer> {
}
