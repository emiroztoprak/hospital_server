package hospital.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EntityScan(basePackages = "hospital.server")
@EnableJpaRepositories(basePackages = {"hospital.server"})
@EnableTransactionManagement
public class SpringHibernateCrudApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringHibernateCrudApplication.class, args);
    }
}
