package net.datasa.ruruplan.gpt.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.ruruplan.gpt.domain.dto.GptCmdDTO;
import net.datasa.ruruplan.gpt.domain.entity.GptCmdEntity;
import net.datasa.ruruplan.member.domain.entity.MemberEntity;
import net.datasa.ruruplan.gpt.repository.GptCmdRepository;
import net.datasa.ruruplan.member.repository.MemberRepository;
import net.datasa.ruruplan.security.AuthenticatedUser;
import org.springframework.stereotype.Service;

/**
 * 질문관련 서비스클래스
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class QuestionService {

    private final GptCmdRepository gptCmdRepository;
    private final MemberRepository memberRepository;


    /**
     * 질문내용 저장 및 cmdNum 반환
     * @param gptCmdDTO 사용자의 답변 내용
     * @return  cmdNum
     */
    public Integer saveAndReturnId(GptCmdDTO gptCmdDTO) {
        GptCmdEntity entity = GptCmdEntity.builder()
                .firstDate(gptCmdDTO.getFirstDate())
                .lastDate(gptCmdDTO.getLastDate())
                .nights(gptCmdDTO.getNights())
                .days(gptCmdDTO.getDays())
                .arrival(gptCmdDTO.getArrival())
                .depart(gptCmdDTO.getDepart())
                .tripType(gptCmdDTO.getTripType())
                .adult(gptCmdDTO.getAdult())
                .children(gptCmdDTO.getChildren())
                .theme1(gptCmdDTO.getTheme1())
                .theme1Weight(gptCmdDTO.getTheme1Weight())
                .theme2(gptCmdDTO.getTheme2())
                .theme2Weight(gptCmdDTO.getTheme2Weight())
                .theme3(gptCmdDTO.getTheme3())
                .theme3Weight(gptCmdDTO.getTheme3Weight())
                .density(gptCmdDTO.getDensity())
                .moveStatus(gptCmdDTO.getMoveStatus())
                .roomCost(gptCmdDTO.getRoomCost())
                .hotel(gptCmdDTO.getHotel())
                .hotelNights(gptCmdDTO.getHotelNights())
                .motel(gptCmdDTO.getMotel())
                .motelNights(gptCmdDTO.getMotelNights())
                .guesthouse(gptCmdDTO.getGuesthouse())
                .guesthouseNights(gptCmdDTO.getGuesthouseNights())
                .hanok(gptCmdDTO.getHanok())
                .hanokNights(gptCmdDTO.getHanokNights())
                .build();
        GptCmdEntity savedEntity = gptCmdRepository.save(entity);

        return savedEntity.getCmdNum();
    }

}


