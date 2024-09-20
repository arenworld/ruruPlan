package net.datasa.ruruplan.plan.repository;

import net.datasa.ruruplan.plan.domain.dto.TaskDTO;
import net.datasa.ruruplan.plan.domain.entity.TaskEntity;

import java.time.LocalTime;
import java.util.List;

public interface TaskRepository {
    List<TaskDTO> getDayTaskList(Integer planNum, Integer dateN);

    List<TaskEntity> getDayLocations(Integer planNum, Integer dateNum);

    List<TaskEntity> dayTaskList(Integer planNum, Integer dayNum);

    List<TaskEntity> updateDurationList(Integer planNum, int dayNum, LocalTime fromPoint);
}
