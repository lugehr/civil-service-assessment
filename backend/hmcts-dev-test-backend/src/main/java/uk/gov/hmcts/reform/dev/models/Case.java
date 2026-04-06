package uk.gov.hmcts.reform.dev.models;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cases")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Case {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Title must not be blank")
    private String title;
    private String description;
    @NotBlank(message = "Status is mandatory")
    private String status;
    @NotNull(message = "Due date is mandatory")
    private LocalDate dueDate;
}
