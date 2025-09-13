package com.biblioteca.catalogo.dto.openlibrary;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LivroResponseDto {

    private String title;

    @Builder.Default
    private List<String> publishers = new ArrayList<>();

    @Builder.Default
    private List<KeyDto> authors = new ArrayList<>();

    @JsonProperty("publish_date")
    @JsonFormat(pattern = "MMMM d, yyyy", locale = "en")
    private LocalDate publishDate;

}
