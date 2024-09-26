package net.datasa.ruruplan.community.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.datasa.ruruplan.community.repository.PlanBoardRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class PlanBoardService {
    final PlanBoardRepository boardRepo;

}
