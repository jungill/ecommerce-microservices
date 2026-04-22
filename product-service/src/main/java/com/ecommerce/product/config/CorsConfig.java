// package com.ecommerce.product.config;

// import org.springframework.context.annotation.Configuration;
// import org.springframework.web.servlet.config.annotation.CorsRegistry;
// import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// /**
//  * Autorise Angular (localhost:4200) à appeler ce service en dev.
//  * En production, le gateway centralise la gestion CORS.
//  */
// @Configuration
// public class CorsConfig implements WebMvcConfigurer {
//     @Override
//     public void addCorsMappings(CorsRegistry registry) {
//         registry.addMapping("/api/**")
//                 .allowedOrigins("http://localhost:4200")
//                 .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
//                 .allowedHeaders("*");
//     }
// }
