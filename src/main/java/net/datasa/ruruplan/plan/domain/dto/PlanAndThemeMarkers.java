package net.datasa.ruruplan.plan.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanAndThemeMarkers {

    private List<PlaceInfoDTO> themeLocations;
    private List<TaskDTO> planLocations;
}
