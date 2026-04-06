package uk.gov.hmcts.reform.dev;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.dev.exceptions.CaseNotFoundException;
import uk.gov.hmcts.reform.dev.models.Case;
import uk.gov.hmcts.reform.dev.repositories.CaseRepository;
import uk.gov.hmcts.reform.dev.services.CaseService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CaseServiceTest {

    @Mock
    private CaseRepository caseRepository;

    @InjectMocks
    private CaseService caseService;

    @Test
    void getAllCasesReturnsAll() {
        Case courtCase1 = new Case(1, "Case 1", "Desc 1", "Open", LocalDate.now());
        Case courtCase2 = new Case(2, "Case 2", "Desc 2", "Open", LocalDate.now());
        when(caseRepository.findAll()).thenReturn(List.of(courtCase1, courtCase2));

        List<Case> result = caseService.getAllCases();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTitle()).isEqualTo("Case 1");
        assertThat(result.get(1).getTitle()).isEqualTo("Case 2");
    }

    @Test
    void getByCaseIdReturnsCase() {
        Case courtCase = new Case(1, "Title", "Desc", "Open", LocalDate.now());
        when(caseRepository.findById(1)).thenReturn(Optional.of(courtCase));

        Case result = caseService.getCaseById(1);

        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getTitle()).isEqualTo("Title");
    }

    @Test
    void getCaseByIdThrowsNotFoundWhenMissing() {
        when(caseRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(CaseNotFoundException.class, () -> caseService.getCaseById(99));
    }

    @Test
    void getCaseByIdThrowsWhenIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> caseService.getCaseById(null));
    }

    @Test
    void createCaseSavesAndReturns() {
        Case courtCase = new Case(null, "New Case", "Desc", "Open", LocalDate.now().plusDays(1));
        Case saved = new Case(1, "New Case", "Desc", "Open", courtCase.getDueDate());
        when(caseRepository.save(courtCase)).thenReturn(saved);

        Case result = caseService.createCase(courtCase);

        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getTitle()).isEqualTo("New Case");
        verify(caseRepository).save(courtCase);
    }

    @Test
    void createCaseThrowsWhenNull() {
        assertThrows(IllegalArgumentException.class, () -> caseService.createCase(null));
    }

    @Test
    void updateStatusSavesNewStatus() {
        Case courtCase = new Case(1, "Title", "Desc", "Open", LocalDate.now());
        when(caseRepository.findById(1)).thenReturn(Optional.of(courtCase));
        when(caseRepository.save(courtCase)).thenReturn(courtCase);

        Case result = caseService.updateCaseStatus(1, "Closed");

        assertThat(result.getStatus()).isEqualTo("Closed");
        verify(caseRepository).save(courtCase);
    }

    @Test
    void updateStatusThrowsNotFoundWhenMissing() {
        when(caseRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(CaseNotFoundException.class, () -> caseService.updateCaseStatus(99, "Closed"));
    }

    @Test
    void updateStatusThrowsWhenIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> caseService.updateCaseStatus(null, "Closed"));
    }

    @Test
    void updateStatusThrowsWhenStatusIsNull() {
        assertThrows(IllegalArgumentException.class, () -> caseService.updateCaseStatus(1, null));
    }

    @Test
    void deleteCaseRemovesCase() {
        when(caseRepository.existsById(1)).thenReturn(true);

        caseService.deleteCase(1);

        verify(caseRepository).deleteById(1);
    }

    @Test
    void deleteCaseThrowsNotFoundWhenMissing() {
        when(caseRepository.existsById(99)).thenReturn(false);

        assertThrows(CaseNotFoundException.class, () -> caseService.deleteCase(99));
        verify(caseRepository, never()).deleteById(99);
    }

    @Test
    void deleteCaseThrowsWhenIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> caseService.deleteCase(null));
    }
}
