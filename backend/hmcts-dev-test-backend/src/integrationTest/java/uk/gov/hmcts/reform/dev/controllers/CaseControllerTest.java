package uk.gov.hmcts.reform.dev.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import uk.gov.hmcts.reform.dev.exceptions.CaseNotFoundException;
import uk.gov.hmcts.reform.dev.models.Case;
import uk.gov.hmcts.reform.dev.services.CaseService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CaseController.class)
class CaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CaseService caseService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllCasesReturnsAll() throws Exception {
        Case courtCase = new Case(1, "Title", "Desc", "Open", LocalDate.now());
        when(caseService.getAllCases()).thenReturn(List.of(courtCase));

        MvcResult result = mockMvc.perform(get("/cases"))
            .andExpect(status().isOk())
            .andReturn();

        List<Case> cases = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            new TypeReference<List<Case>>() {}
        );

        assertThat(cases).hasSize(1);
        assertThat(cases.get(0).getTitle()).isEqualTo("Title");
    }

    @Test
    void getByCaseIdReturnsCase() throws Exception {
        Case courtCase = new Case(1, "Title", "Desc", "Open", LocalDate.now());
        when(caseService.getCaseById(1)).thenReturn(courtCase);

        MvcResult result = mockMvc.perform(get("/cases/1"))
            .andExpect(status().isOk())
            .andReturn();

        Case returned = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            Case.class
        );

        assertThat(returned.getId()).isEqualTo(1);
        assertThat(returned.getTitle()).isEqualTo("Title");
    }

    @Test
    void getCaseByIdReturns404WhenMissing() throws Exception {
        when(caseService.getCaseById(99)).thenThrow(new CaseNotFoundException(99));

        MvcResult result = mockMvc.perform(get("/cases/99"))
            .andExpect(status().isNotFound())
            .andReturn();

        Map<String, String> error = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            new TypeReference<Map<String, String>>() {}
        );

        assertThat(error.get("error")).isEqualTo("Case with id 99 not found");
    }

    @Test
    @SuppressWarnings("null")
    void createCaseReturnsCreatedCase() throws Exception {
        Case courtCase = new Case(null, "New Case", "Desc", "Open", LocalDate.now().plusDays(1));
        Case saved = new Case(1, "New Case", "Desc", "Open", courtCase.getDueDate());
        when(caseService.createCase(any())).thenReturn(saved);

        MvcResult result = mockMvc.perform(post("/cases")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(courtCase)))
            .andExpect(status().isCreated())
            .andReturn();

        Case returned = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            Case.class
        );

        assertThat(returned.getId()).isEqualTo(1);
        assertThat(returned.getTitle()).isEqualTo("New Case");
    }

    @Test
    void createCaseReturnsBadRequestOnMalformedJson() throws Exception {
        MvcResult result = mockMvc.perform(post("/cases")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("this is not json"))
            .andExpect(status().isBadRequest())
            .andReturn();

        Map<String, Object> error = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            new TypeReference<Map<String, Object>>() {}
        );

        assertThat(error.get("message")).isEqualTo("Malformed JSON request");
    }

    @Test
    @SuppressWarnings("null")
    void updateStatusReturnsUpdatedCase() throws Exception {
        Case updated = new Case(1, "Title", "Desc", "In Progress", LocalDate.now());
        when(caseService.updateCaseStatus(eq(1), eq("In Progress"))).thenReturn(updated);

        MvcResult result = mockMvc.perform(patch("/cases/1/status")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{\"status\":\"In Progress\"}"))
            .andExpect(status().isOk())
            .andReturn();

        Case returned = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            Case.class
        );

        assertThat(returned.getStatus()).isEqualTo("In Progress");
    }

    @Test
    void deleteCaseReturnsNoContent() throws Exception {
        doNothing().when(caseService).deleteCase(1);

        mockMvc.perform(delete("/cases/1"))
            .andExpect(status().isNoContent());
    }

    @Test
    void deleteCaseReturns404WhenMissing() throws Exception {
        doThrow(new CaseNotFoundException(99)).when(caseService).deleteCase(99);

        MvcResult result = mockMvc.perform(delete("/cases/99"))
            .andExpect(status().isNotFound())
            .andReturn();

        Map<String, String> error = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            new TypeReference<Map<String, String>>() {}
        );

        assertThat(error.get("error")).isEqualTo("Case with id 99 not found");
    }
}
