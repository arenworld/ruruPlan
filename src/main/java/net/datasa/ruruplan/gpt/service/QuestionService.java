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
    public GptCmdDTO saveAndReturnDTO(GptCmdDTO gptCmdDTO) {

        MemberEntity member = memberRepository.findById(gptCmdDTO.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("해당 멤버의 정보가 존재하지 않습니다: " + gptCmdDTO.getMemberId()));

        GptCmdEntity entity = GptCmdEntity.builder()
                .member(member)
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
        GptCmdDTO savedCmdDTO = GptCmdDTO.builder()
                .cmdNum(savedEntity.getCmdNum())
                .memberId(savedEntity.getMember().getMemberId())
                .firstDate(savedEntity.getFirstDate())
                .lastDate(savedEntity.getLastDate())
                .nights(savedEntity.getNights())
                .days(savedEntity.getDays())
                .arrival(savedEntity.getArrival())
                .depart(savedEntity.getDepart())
                .tripType(savedEntity.getTripType())
                .adult(savedEntity.getAdult())
                .children(savedEntity.getChildren())
                .theme1(savedEntity.getTheme1())
                .theme1Weight(savedEntity.getTheme1Weight())
                .theme2(savedEntity.getTheme2())
                .theme2Weight(savedEntity.getTheme2Weight())
                .theme3(savedEntity.getTheme3())
                .theme3Weight(savedEntity.getTheme3Weight())
                .density(savedEntity.getDensity())
                .moveStatus(savedEntity.getMoveStatus())
                .roomCost(savedEntity.getRoomCost())
                .hotel(savedEntity.getHotel())
                .hotelNights(savedEntity.getHotelNights())
                .motel(savedEntity.getMotel())
                .motelNights(savedEntity.getMotelNights())
                .guesthouse(savedEntity.getGuesthouse())
                .guesthouseNights(savedEntity.getGuesthouseNights())
                .hanok(savedEntity.getHanok())
                .hanokNights(savedEntity.getHanokNights())
                .build();

        return savedCmdDTO;
    }
}


