package net.datasa.ruruplan.community.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.datasa.ruruplan.community.repository.SavePlanRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class SavePlanService {
    final SavePlanRepository saveRepo;
}
