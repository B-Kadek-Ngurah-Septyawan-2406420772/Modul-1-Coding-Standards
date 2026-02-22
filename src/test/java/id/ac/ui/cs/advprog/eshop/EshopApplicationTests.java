package id.ac.ui.cs.advprog.eshop;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootTest
class EshopApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void mainRunsSpringApplication() {
        String[] args = {"--spring.main.web-application-type=none"};
        ConfigurableApplicationContext applicationContext = Mockito.mock(ConfigurableApplicationContext.class);

        try (MockedStatic<SpringApplication> springApplication = Mockito.mockStatic(SpringApplication.class)) {
            springApplication.when(() -> SpringApplication.run(EshopApplication.class, args)).thenReturn(applicationContext);

            EshopApplication.main(args);

            springApplication.verify(() -> SpringApplication.run(EshopApplication.class, args));
        }
    }

}
