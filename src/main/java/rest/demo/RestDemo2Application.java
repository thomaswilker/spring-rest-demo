package rest.demo;
import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.paths.RelativePathProvider;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableJpaRepositories(basePackages="rest.demo.repository.jpa")
@EnableSpringDataWebSupport
@EnableAspectJAutoProxy(proxyTargetClass=true)
@EnableAsync(proxyTargetClass=true)
@EnableElasticsearchRepositories(basePackages = "rest.demo.repository.es")
@EnableTransactionManagement(proxyTargetClass=true)
@SpringBootApplication
@Import({springfox.documentation.spring.data.rest.configuration.SpringDataRestConfiguration.class,
		 springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration.class})
@EnableSwagger2
public class RestDemo2Application {

	public static void main(String[] args) {
		SpringApplication.run(RestDemo2Application.class, args);
	}
	
	@Autowired
	private ServletContext context;
	
	@Bean
	public Docket swaggerConfig() {
		
		return new Docket(DocumentationType.SWAGGER_2)
					.select()
					.paths(PathSelectors.ant("/api/**"))
					.build();
					
	}
}
