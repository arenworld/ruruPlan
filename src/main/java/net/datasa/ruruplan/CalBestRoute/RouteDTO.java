package net.datasa.ruruplan.CalBestRoute;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteDTO {

    String sx;
    String sy;
    String ex;
    String ey;
}
