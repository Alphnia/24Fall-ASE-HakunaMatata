package app;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * this class is for frontend showing.
*/
@Configuration
public class WebConfig {
  /**
   * this class is for frontend showing.
  */
  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
          @Override
          public void addCorsMappings(CorsRegistry registry) {
            registry.addMapping("/**") // Apply to all endpoints
                      .allowedOrigins("http://localhost:5173") // Allow the frontend origin
                      .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS") 
                      // Allow specific HTTP methods
                      .allowedHeaders("*") // Allow all headers
                      .allowCredentials(true); // Allow credentials if needed
          }
      };
  }
}
