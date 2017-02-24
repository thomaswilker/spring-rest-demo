package rest.demo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableJpaRepositories(basePackages="rest.demo.repository.jpa")
@EnableSpringDataWebSupport
@EnableAspectJAutoProxy(proxyTargetClass=true)
@EnableAsync(proxyTargetClass=true)
@EnableElasticsearchRepositories(basePackages = "rest.demo.repository.es")
@EnableTransactionManagement(proxyTargetClass=true)
@SpringBootApplication
public class RestDemo2Application {

	public static void main(String[] args) {
		SpringApplication.run(RestDemo2Application.class, args);
	}
	
}
