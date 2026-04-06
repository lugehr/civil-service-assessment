package uk.gov.hmcts.reform.dev;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import uk.gov.hmcts.reform.dev.models.Case;
import uk.gov.hmcts.reform.dev.repositories.CaseRepository;

import java.time.LocalDate;

@Component
public class DataLoader implements CommandLineRunner {

    private final CaseRepository caseRepository;

    public DataLoader(CaseRepository caseRepository) {
        this.caseRepository = caseRepository;
    }

    @Override
    public void run(String... args) {
        caseRepository.save(new Case(null, "Smith v Jones", "Personal injury claim", "Open",
            LocalDate.now().plusDays(30)));
        caseRepository.save(new Case(null, "Crown v Davies", "Criminal proceedings", "In Progress",
            LocalDate.now().plusDays(14)));
        caseRepository.save(new Case(null, "Brown Estate", "Probate dispute", "Open",
            LocalDate.now().plusDays(60)));
        caseRepository.save(new Case(null, "Taylor Divorce", "Family law proceedings", "Closed",
            LocalDate.now().minusDays(5)));
    }
}
