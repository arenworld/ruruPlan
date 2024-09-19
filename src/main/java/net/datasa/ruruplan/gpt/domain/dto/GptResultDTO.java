package net.datasa.ruruplan.gpt.domain.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GptResultDTO {
    private Integer cmdNum;
    private List<String> placeIdList;
}
