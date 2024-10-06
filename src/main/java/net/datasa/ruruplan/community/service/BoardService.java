package net.datasa.ruruplan.community.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.ruruplan.community.domain.dto.BoardDTO;
import net.datasa.ruruplan.community.domain.dto.ReplyDTO;
import net.datasa.ruruplan.community.domain.entity.BoardEntity;
import net.datasa.ruruplan.community.domain.entity.PlanBoardEntity;
import net.datasa.ruruplan.community.domain.entity.ReplyEntity;
import net.datasa.ruruplan.community.domain.entity.SavePlanEntity;
import net.datasa.ruruplan.community.repository.BoardRepository;
import net.datasa.ruruplan.community.repository.PlanBoardRepository;
import net.datasa.ruruplan.community.repository.ReplyRepository;
import net.datasa.ruruplan.community.repository.SavePlanRepository;
import net.datasa.ruruplan.member.domain.entity.MemberEntity;
import net.datasa.ruruplan.member.repository.MemberRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 게시판 관련 서비스
 */
@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class BoardService {

    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final ReplyRepository replyRepository;
    private final SavePlanRepository savePlanRepository;
    private final PlanBoardRepository planBoardRepository;

    /**
     * 게시판 글 저장
     *
     * @param boardDTO   저장할 글 정보
     * @param uploadPath 파일 업로드 경로
     * @param upload     첨부 파일
     */
    public void write(BoardDTO boardDTO, String uploadPath, MultipartFile upload) {
        // 작성자 정보 조회
        MemberEntity memberEntity = memberRepository.findById(boardDTO.getMemberId())
                .orElseThrow(() -> new EntityNotFoundException("회원 아이디가 없습니다."));

        // 게시글 엔티티 생성
        BoardEntity entity = new BoardEntity();
        entity.setMember(memberEntity);

        entity.setContents(boardDTO.getContents());

        // 첨부파일 처리
        if (upload != null && !upload.isEmpty()) {
            File directoryPath = new File(uploadPath);
            if (!directoryPath.isDirectory()) {
                directoryPath.mkdirs();
            }

            String originalName = upload.getOriginalFilename();
            String extension = originalName.substring(originalName.lastIndexOf("."));
            String dateString = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String uuidString = UUID.randomUUID().toString();
            String fileName = dateString + "-" + uuidString + extension;

            try {
                File filePath = new File(uploadPath + "/" + fileName);
                upload.transferTo(filePath);
                entity.setOriginalName(originalName);
                entity.setFileName(fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        boardRepository.save(entity);
    }

    /**
     * 게시글 전체 조회
     *
     * @return 글 목록
     */
    public List<BoardDTO> getListAll() {
        List<BoardEntity> entityList = boardRepository.findAllByOrderByBoardNumDesc();
        List<BoardDTO> dtoList = new ArrayList<>();
        for (BoardEntity entity : entityList) {
            BoardDTO dto = convertToDTO(entity);

            // 댓글 목록 설정
            List<ReplyDTO> replyDTOList = new ArrayList<>();
            for (ReplyEntity replyEntity : entity.getReplyList()) {
                ReplyDTO replyDTO = convertToReplyDTO(replyEntity);
                replyDTOList.add(replyDTO);
            }
            dto.setReplyList(replyDTOList);

            dtoList.add(dto);
        }
        return dtoList;
    }


    /**
     * 게시글 1개 조회
     *
     * @param boardNum 글번호
     * @return the BoardDTO 글 정보
     * @throws EntityNotFoundException 게시글이 없을 때 예외
     */
    public BoardDTO getBoard(int boardNum) {
        BoardEntity entity = boardRepository.findById(boardNum)
                .orElseThrow(() -> new EntityNotFoundException("해당 번호의 글이 없습니다."));

        entity.setViewCount(entity.getViewCount() + 1);

        BoardDTO dto = convertToDTO(entity);
        log.debug("게시글 내용: {}", dto.getContents());
        log.debug("작성일자: {}", dto.getCreateDate());
        // 댓글 목록 설정
        List<ReplyDTO> replyDTOList = new ArrayList<>();
        for (ReplyEntity replyEntity : entity.getReplyList()) {
            ReplyDTO replyDTO = convertToReplyDTO(replyEntity);
            replyDTOList.add(replyDTO);
        }
        dto.setReplyList(replyDTOList);
        dto.setCreateDate(entity.getCreateDate());
        dto.setContents(entity.getContents());

        return dto;
    }


    /**
     * 게시글 삭제
     *
     * @param boardNum   삭제할 글번호
     * @param username   로그인한 아이디
     * @param uploadPath 첨부파일이 저장된 경로
     */
    public void delete(int boardNum, String username, String uploadPath) {
        BoardEntity boardEntity = boardRepository.findById(boardNum)
                .orElseThrow(() -> new EntityNotFoundException("게시글이 없습니다."));

        if (!boardEntity.getMember().getMemberId().equals(username)) {
            throw new RuntimeException("삭제 권한이 없습니다.");
        }

        // 첨부파일 삭제
        if (boardEntity.getFileName() != null && !boardEntity.getFileName().isEmpty()) {
            File file = new File(uploadPath, boardEntity.getFileName());
            if (file.exists()) {
                file.delete();
            }
        }

        boardRepository.delete(boardEntity);
    }

    /**
     * 게시글 수정
     *
     * @param boardDTO   수정할 글정보
     * @param username   로그인한 아이디
     * @param uploadPath 파일 업로드 경로
     * @param upload     첨부 파일
     */
    public void update(BoardDTO boardDTO, String username, String uploadPath, MultipartFile upload) {
        BoardEntity boardEntity = boardRepository.findById(boardDTO.getBoardNum())
                .orElseThrow(() -> new EntityNotFoundException("게시글이 없습니다."));

        if (!boardEntity.getMember().getMemberId().equals(username)) {
            throw new RuntimeException("수정 권한이 없습니다.");
        }

        // 첨부파일 처리
        if (upload != null && !upload.isEmpty()) {
            // 기존 파일 삭제
            if (boardEntity.getFileName() != null && !boardEntity.getFileName().isEmpty()) {
                File oldFile = new File(uploadPath, boardEntity.getFileName());
                if (oldFile.exists()) {
                    oldFile.delete();
                }
            }

            // 새 파일 업로드
            String originalName = upload.getOriginalFilename();
            String extension = originalName.substring(originalName.lastIndexOf("."));
            String dateString = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String uuidString = UUID.randomUUID().toString();
            String fileName = dateString + "-" + uuidString + extension;

            try {
                File filePath = new File(uploadPath + "/" + fileName);
                upload.transferTo(filePath);
                boardEntity.setOriginalName(originalName);
                boardEntity.setFileName(fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 게시글 내용 수정

        boardEntity.setContents(boardDTO.getContents());
    }

    /**
     * 댓글 작성
     *
     * @param replyDTO 작성한 댓글 정보
     */
    public void replyWrite(ReplyDTO replyDTO) {
        MemberEntity memberEntity = memberRepository.findById(replyDTO.getMemberId())
                .orElseThrow(() -> new EntityNotFoundException("사용자 아이디가 없습니다."));

        BoardEntity boardEntity = boardRepository.findById(replyDTO.getBoardNum())
                .orElseThrow(() -> new EntityNotFoundException("게시글이 없습니다."));

        ReplyEntity entity = ReplyEntity.builder()
                .board(boardEntity)
                .member(memberEntity)
                .contents(replyDTO.getContents())
                .build();

        replyRepository.save(entity);
    }


    /**
     * 댓글 삭제
     *
     * @param replyNum 삭제할 댓글 번호
     * @param username 로그인한 아이디
     */
    public void replyDelete(Integer replyNum, String username) {
        ReplyEntity replyEntity = replyRepository.findById(replyNum)
                .orElseThrow(() -> new EntityNotFoundException("댓글이 없습니다."));

        if (!replyEntity.getMember().getMemberId().equals(username)) {
            throw new RuntimeException("삭제 권한이 없습니다.");
        }
        replyRepository.delete(replyEntity);
    }

    /**
     * 좋아요 기능
     *
     * @param boardNum 글번호
     * @return 좋아요 수
     */
    public Integer like(Integer boardNum) {
        BoardEntity entity = boardRepository.findById(boardNum)
                .orElseThrow(() -> new EntityNotFoundException("없는 번호입니다."));
        log.debug("서비스에서 전달받은 글번호:{}", boardNum);

        entity.setLikeCount(entity.getLikeCount() + 1);
        Integer likeCount = entity.getLikeCount();
        log.debug("추천수:{}", likeCount);

        return likeCount;
    }
    // 댓글 목록 조회
    public List<ReplyDTO> getReplyList(int boardNum) {
        List<ReplyEntity> replyEntities = replyRepository.findByBoard_BoardNum(boardNum, Sort.by(Sort.Direction.ASC, "createDate"));
        List<ReplyDTO> replyDTOList = new ArrayList<>();
        for (ReplyEntity entity : replyEntities) {
            ReplyDTO dto = convertToReplyDTO(entity);
            replyDTOList.add(dto);
        }
        return replyDTOList;
    }

    /**
     * BoardEntity를 BoardDTO로 변환
     *
     * @param entity 게시글 엔티티
     * @return 게시글 DTO
     */
    private BoardDTO convertToDTO(BoardEntity entity) {
        return BoardDTO.builder()
                .boardNum(entity.getBoardNum())
                .memberId(entity.getMember().getMemberId())
                .nickname(entity.getMember().getNickname())

                .profileImageUrl(entity.getMember().getProfileImageUrl())
                .contents(entity.getContents())
                .viewCount(entity.getViewCount())
                .likeCount(entity.getLikeCount())
                .originalName(entity.getOriginalName())
                .fileName(entity.getFileName())
                .createDate(entity.getCreateDate())
                .updateDate(entity.getUpdateDate())
                .build();
    }

    /**
     * ReplyEntity를 ReplyDTO로 변환
     *
     * @param entity 댓글 엔티티
     * @return 댓글 DTO
     */
    private ReplyDTO convertToReplyDTO(ReplyEntity entity) {
        return ReplyDTO.builder()
                .replyNum(entity.getReplyNum())
                .boardNum(entity.getBoard().getBoardNum())
                .memberId(entity.getMember().getMemberId())
                .nickname(entity.getMember().getNickname())
                .contents(entity.getContents())
                .createDate(entity.getCreateDate())
                .build();
    }

    public void savePlan(int boardNum, String userId) {
        PlanBoardEntity planBoardEntity = planBoardRepository.findById(boardNum)
                .orElseThrow(() -> new EntityNotFoundException("boardNum에 해당되는 엔터티가 없습니다."));

        MemberEntity memberEntity = memberRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("userId에 해당되는 회원이 없습니다."));

        SavePlanEntity savePlanEntity = SavePlanEntity.builder()
                .planBoard(planBoardEntity)
                .member(memberEntity)
                .build();

        savePlanRepository.save(savePlanEntity);
    }
}
