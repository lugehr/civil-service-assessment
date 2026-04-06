package uk.gov.hmcts.reform.dev.exceptions;

public class CaseNotFoundException extends RuntimeException {
    public CaseNotFoundException(Integer id) {
        super("Case with id " + id + " not found");
    }
}
