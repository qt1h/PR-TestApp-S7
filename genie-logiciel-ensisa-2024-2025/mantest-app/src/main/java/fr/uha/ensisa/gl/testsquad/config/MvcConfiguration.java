package fr.uha.ensisa.gl.testsquad.config;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.EncodedResourceResolver;
import org.springframework.web.servlet.resource.PathResourceResolver;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;

import fr.uha.ensisa.gl.testsquad.mantest.ManualTest;
import fr.uha.ensisa.gl.testsquad.mantest.ManualTestExecution;
import fr.uha.ensisa.gl.testsquad.mantest.dao.DaoFactory;
import fr.uha.ensisa.gl.testsquad.mantest.dao.mem.DaoFactoryMem;
import fr.uha.ensisa.gl.testsquad.mantest.status.StepStatus;

@Configuration
@ComponentScan(basePackages="fr.uha.ensisa.gl.testsquad")
@EnableWebMvc
public class MvcConfiguration implements WebMvcConfigurer {

	@Autowired
	private ApplicationContext applicationContext;

	@Bean
	public DaoFactory getDaoFactory() {

		String ecobench = System.getenv("ECOBENCH");

		if ("True".equalsIgnoreCase(ecobench)) {
			DaoFactory ret = new DaoFactoryMem();

			final int NOMBRE_TEST = 50;

			for (int i = 1; i <= NOMBRE_TEST; i++) {

				ManualTest manualStepTest = new ManualTest(i, "test_" + i, "");
				ManualTest.Step step = new ManualTest.Step("step_1");
				ManualTest.Step step2 = new ManualTest.Step("step_2");

				manualStepTest.addStep(step);
				manualStepTest.addStep(step2);

				ret.getTestDao().persist(manualStepTest);

				manualStepTest.getSteps().get(0).setStatus(StepStatus.ACCEPTED);
				manualStepTest.getSteps().get(1).setStatus(StepStatus.ACCEPTED);
				ManualTestExecution manualTestExecution = new ManualTestExecution(0, manualStepTest, LocalDateTime.now());
				manualTestExecution.setComment("c'est ok");

				manualStepTest.getSteps().get(0).setStatus(StepStatus.REFUSED);
				manualStepTest.getSteps().get(1).setStatus(StepStatus.UNDEFINED);
				ManualTestExecution manualTestExecution2 = new ManualTestExecution(0, manualStepTest, LocalDateTime.now());
				manualTestExecution2.setComment("erreur step_1");
				manualTestExecution2.getSteps().get(0).setComment("erreur step_1");

				ret.getTestExecutionDao().persist(manualTestExecution);
				ret.getTestExecutionDao().persist(manualTestExecution2);

			}

			return ret;
		}

		return new DaoFactoryMem();

	}

	@Bean
    public ViewResolver viewResolver() {
		ThymeleafViewResolver resolver = new ThymeleafViewResolver();
		resolver.setTemplateEngine(springTemplateEngine());
		return resolver;
	}

	@Bean
	public SpringTemplateEngine springTemplateEngine() {
	  SpringTemplateEngine engine = new SpringTemplateEngine();
	  engine.setEnableSpringELCompiler(true);
	  engine.setTemplateResolver(templateResolver());
	  return engine;
	}

	@Bean
    public SpringResourceTemplateResolver templateResolver() {
	  SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
	  resolver.setApplicationContext(this.applicationContext);
	  resolver.setPrefix("/WEB-INF/views/");
	  resolver.setSuffix(".html");
	  resolver.setTemplateMode(TemplateMode.HTML);
	  return resolver;
	}
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry
			.addResourceHandler("/resources/**")
			.addResourceLocations("/resources/")
			.setCachePeriod(0); // for development
		registry
			.addResourceHandler("/libs/**")
			.addResourceLocations("/libs/")
			.setCachePeriod((int)TimeUnit.DAYS.toSeconds(365))
			.resourceChain(true)
			.addResolver(new EncodedResourceResolver())
      		.addResolver(new PathResourceResolver());
	}

	@Bean
	public MultipartResolver multipartResolver(){
		return new StandardServletMultipartResolver();
	}
}
