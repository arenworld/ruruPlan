package net.datasa.ruruplan.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // /uploads/** 경로로 접근하는 파일 요청을 실제로는 c:/tempUpload/ 폴더에 있는 파일로 매핑
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:///c:/tempUpload/");

        // /images/planCoverImage/** 경로로 접근하는 파일 요청을 uploads/images/planCoverImage/로 매핑
        registry.addResourceHandler("/images/planCoverImage/**")
                .addResourceLocations("file:uploads/images/planCoverImage/");

    }


}
