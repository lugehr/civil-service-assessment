package uk.gov.hmcts.reform.dev.services;

import java.util.List;

import org.springframework.stereotype.Service;

import uk.gov.hmcts.reform.dev.exceptions.CaseNotFoundException;
import uk.gov.hmcts.reform.dev.models.Case;
import uk.gov.hmcts.reform.dev.repositories.CaseRepository;

@Service
public class CaseService {
    private final CaseRepository caseRepository;

    public CaseService(CaseRepository caseRepository) {
        this.caseRepository = caseRepository;
    }

    public List<Case> getAllCases() {
        return caseRepository.findAll();
    }

    public Case getCaseById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("Case ID cannot be null");
        }
        return caseRepository.findById(id).orElseThrow(() -> new CaseNotFoundException(id));
    }

    public Case createCase(Case courtCase) {
        if (courtCase == null) {
            throw new IllegalArgumentException("Case cannot be null");
        }
        return caseRepository.save(courtCase);
    }

    public Case updateCaseStatus(Integer id, String status) {
        if (id == null) {
            throw new IllegalArgumentException("Case ID cannot be null");
        }
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        Case courtCase = getCaseById(id);
        courtCase.setStatus(status);
        return caseRepository.save(courtCase);
    }

    public void deleteCase(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("Case ID cannot be null");
        }
        if (!caseRepository.existsById(id)) {
            throw new CaseNotFoundException(id);
        }
        caseRepository.deleteById(id);
    }
}
