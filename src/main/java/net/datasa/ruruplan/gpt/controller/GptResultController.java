package net.datasa.ruruplan.gpt.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import net.datasa.ruruplan.gpt.domain.dto.GptCmdDTO;
//import net.datasa.ruruplan.gpt.service.GptResultService;
import net.datasa.ruruplan.gpt.domain.dto.GptResultDTO;
import net.datasa.ruruplan.gpt.service.GptResultService;
import net.datasa.ruruplan.plan.domain.dto.PlanDTO;
import net.datasa.ruruplan.plan.domain.dto.TaskDTO;
import net.datasa.ruruplan.security.AuthenticatedUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class GptResultController {

    private final GptResultService gptResultService;


    /**
     * 로딩창 ajax에서 요청을 받아 planDTO를 만드는 메서드
     * @param cmdNum    로딩창에서 ajax로 받아온 값
     * @param session   세션에 planDTO를 저장할 예정
     * @return  리다이렉트 및 페이지 이동 요청
     * @throws IOException  책임회피
     */
    @PostMapping("/getGptPlan")
    public String getGptPlan(@RequestParam("cmdNum") Integer cmdNum, HttpSession session) throws IOException {
        log.debug("cmdNum: {}", cmdNum);

        // GPT API 호출 및 PlanDTO 생성
        GptResultDTO result = gptResultService.gptApi(cmdNum);
        PlanDTO plan = gptResultService.planCreate(result);

        // PlanDTO를 세션에 저장
        session.setAttribute("planDTO", plan);

        // 여기서 리다이렉트는 브라우저에게 /gptView/result로 요청을 보내라고 알려주는 것
        return "redirect:/gptView/result";
    }

    /**
     * 세션값에 저장된 PlanDTO를 모델에 넣어 gptResult.html로 보내는 메서드
     * @param session   저장된 PlanDTO불러오기 위함
     * @param model     planDTO를 모델값에 저장
     * @return          결과창으로 페이지 이동
     */
    @GetMapping("/gptView/result")
    public String getGptResultPage(HttpSession session, Model model) {
        // 세션에서 PlanDTO를 가져옴
        PlanDTO planDTO = (PlanDTO) session.getAttribute("planDTO");
        Integer cmdNum = planDTO.getCmdNum();
        GptCmdDTO gptCmdDTO = gptResultService.findGptCmdDTO(cmdNum);

        model.addAttribute("cmdDTO", gptCmdDTO);

        // theme1, theme2, theme3을 배열로 묶기
        List<String> themeArray = Arrays.asList(planDTO.getTheme1(), planDTO.getTheme2(), planDTO.getTheme3());

        // 모델에 추가
        model.addAttribute("themeArray", themeArray);


        LocalDate startDate = planDTO.getStartDate();
        LocalDate endDate = planDTO.getEndDate();
        long lastDay = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        model.addAttribute("lastDay", lastDay);

        // 일자별로 TaskDTO 리스트를 분류하기 위한 Map
        Map<Integer, List<TaskDTO>> taskByDateMap = new HashMap<>();

        // planDTO 안의 taskList를 반복하면서 dateN에 따라 리스트를 분류
        for (TaskDTO task : planDTO.getTaskList()) {
            Integer dateN = task.getDateN();

            // 해당 dateN에 해당하는 리스트가 없으면 새로 생성
            taskByDateMap.putIfAbsent(dateN, new ArrayList<>());

            // 해당 날짜의 리스트에 task 추가
            taskByDateMap.get(dateN).add(task);
        }

        // 일자별 taskList를 모델에 추가
        model.addAttribute("taskByDateMap", taskByDateMap);

        // 모델에 PlanDTO를 추가하여 화면에서 사용할 수 있게 함
        model.addAttribute("planDTO", planDTO);

        // gptResult.html 페이지로 이동
        return "gptView/gptResult";
    }

    // 임시로 만든 것이라 나중에 삭제하고 다른 곳으로 옮길 예정.
    @GetMapping("gptView/question")
    public String gptViewQuestion() {
        return "gptView/question";
    }

    /**
     * gpt답변 커스터마이징하기 버튼 누르면 plan저장 후 customPlan 페이지 로드
     * @param planDTO
     * @param model
     * @return
     */
//    @PostMapping("customGptPlan")
//    public String customGptPlan(@ModelAttribute PlanDTO planDTO, Model model) {
//        gptResultService.saveGptPlan(planDTO);
//        Integer planNum = gptResultService.getPlanNum(planDTO.getPlanNum());
//        model.addAttribute("planNum", planNum);
//        return "customView/customPlan";
//    }
//
//    @GetMapping("reGptPlan")
//    public String reGptPlan(Model model) {
//
//    }


    @PostMapping("/saveGptPlan")
    @ResponseBody
    public void saveGptPlan(@RequestBody PlanDTO planDTO, @AuthenticationPrincipal AuthenticatedUser user) {     // Json 데이터 받을때는 RequestBody 사용해야함
        gptResultService.saveGptPlan(planDTO, user.getId());
    }

    @PostMapping("/uploadCoverImage")
    @ResponseBody
    public String uploadCoverImage(@RequestParam("file") MultipartFile file) {
        try {
            // 파일이 비어있는지 확인
            if (file.isEmpty()) {
                return "파일이 비어 있습니다.";
            }

            // 원본 파일 이름 가져오기
            String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());

            // 고유한 파일 이름 생성
            String fileName = UUID.randomUUID().toString() + "_" + originalFilename;

            // 저장할 디렉토리 설정 (애플리케이션 외부 디렉토리)
            String uploadDir = "uploads/images/planCoverImage/";

            // 디렉토리가 없으면 생성
            File uploadDirFile = new File(uploadDir);
            if (!uploadDirFile.exists()) {
                uploadDirFile.mkdirs();
            }

            // 이미지 읽기
            BufferedImage originalImage = ImageIO.read(file.getInputStream());

            // 목표 크기 설정
            int targetWidth = 748;
            int targetHeight = 1023;

            // 이미지 리사이즈 및 크롭
            BufferedImage processedImage = resizeAndCropImage(originalImage, targetWidth, targetHeight);

            // 이미지 저장 (압축 품질 설정)
            File outputFile = new File(uploadDir + fileName);
            saveCompressedImage(processedImage, outputFile, 0.75f); // 품질을 75%로 설정

            // 저장된 이미지의 경로 반환
            String filePath = "/images/planCoverImage/" + fileName; // 클라이언트에서 접근 가능한 경로

            return filePath;

        } catch (IOException e) {
            e.printStackTrace();
            return "이미지 처리 중 오류가 발생했습니다.";
        }
    }

    // 이미지 리사이즈 및 크롭을 위한 헬퍼 메서드
    private BufferedImage resizeAndCropImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        // 리사이즈를 위한 스케일 계산
        double widthScale = (double) targetWidth / width;
        double heightScale = (double) targetHeight / height;
        double scale = Math.max(widthScale, heightScale); // 이미지가 목표 영역을 덮도록 스케일 설정

        // 새로운 크기 계산
        int newWidth = (int) (scale * width);
        int newHeight = (int) (scale * height);

        // 이미지 리사이즈
        Image resizedImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);

        // 리사이즈된 이미지를 BufferedImage로 변환
        BufferedImage bufferedResizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = bufferedResizedImage.createGraphics();
        g2d.drawImage(resizedImage, 0, 0, null);
        g2d.dispose();

        // 이미지 크롭
        int x = (newWidth - targetWidth) / 2;
        int y = (newHeight - targetHeight) / 2;
        BufferedImage croppedImage = bufferedResizedImage.getSubimage(x, y, targetWidth, targetHeight);

        return croppedImage;
    }

    // 이미지 압축하여 저장하는 메서드
    private void saveCompressedImage(BufferedImage image, File file, float quality) throws IOException {
        // 이미지 쓰기 위한 ImageWriter 가져오기
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
        if (!writers.hasNext()) {
            throw new IllegalStateException("JPEG 지원하는 ImageWriter가 없습니다.");
        }
        ImageWriter writer = writers.next();

        // 출력 파일 설정
        try (ImageOutputStream ios = ImageIO.createImageOutputStream(file)) {
            writer.setOutput(ios);

            // 압축 품질 설정
            ImageWriteParam param = writer.getDefaultWriteParam();
            if (param.canWriteCompressed()) {
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(quality); // 0.0f ~ 1.0f 사이의 값 (낮을수록 압축률 증가)
            }

            // 이미지 쓰기
            writer.write(null, new IIOImage(image, null, null), param);
        } finally {
            writer.dispose();
        }
    }

}

