package uk.gov.hmcts.reform.dev.controllers;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import uk.gov.hmcts.reform.dev.models.Case;
import uk.gov.hmcts.reform.dev.services.CaseService;

@RestController
@RequestMapping("/cases")
@CrossOrigin(origins = "http://localhost:3000")
public class CaseController {
    private final CaseService service;

    public CaseController(CaseService service) {
        this.service = service;
    }

    // Endpoint to GET all cases
    @GetMapping
    public List<Case> getAllCases() {
        return service.getAllCases();
    }

    // Endpoint to GET a case by ID
    @GetMapping("/{id}")
    public Case getCaseById(@PathVariable Integer id) {
        return service.getCaseById(id);
    }

    // Endpoint to POST a new case
    @PostMapping
    @SuppressWarnings("null")
    public ResponseEntity<Case> createCase(@Valid @NotNull @RequestBody Case courtCase) {
        Case created = service.createCase(courtCase);
        return ResponseEntity.created(URI.create("/cases/" + created.getId())).body(created);
    }

    // Endpoint to PATCH (update) a case's status
    @PatchMapping("/{id}/status")
    public Case updateCaseStatus(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        return service.updateCaseStatus(id, body.get("status"));
    }

    // Endpoint to DELETE a case by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCase(@PathVariable Integer id) {
        service.deleteCase(id);
        return ResponseEntity.noContent().build();
    }
}
