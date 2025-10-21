package com.fitbuddy.fitbudd.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class ThymeleafConfig {

    @Value("${spring.thymeleaf.prefix}")
    private String templatePrefix;

    @Value("${spring.thymeleaf.suffix}")
    private String templateSuffix;

    @Value("${spring.thymeleaf.mode}")
    private String templateMode;

    @Value("${spring.thymeleaf.encoding}")
    private String templateEncoding;

    @Bean
    public SpringTemplateEngine emailTemplateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.addTemplateResolver(htmlTemplateResolver());
        return templateEngine;
    }

    private ITemplateResolver htmlTemplateResolver() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();

        // Remove 'classpath:' from the prefix
        String prefix = templatePrefix.replace("classpath:", "");
        templateResolver.setPrefix(prefix);

        templateResolver.setSuffix(templateSuffix);
        templateResolver.setTemplateMode(templateMode);
        templateResolver.setCharacterEncoding(templateEncoding);
        templateResolver.setCacheable(false);  // During development, set to false
        return templateResolver;
    }
}