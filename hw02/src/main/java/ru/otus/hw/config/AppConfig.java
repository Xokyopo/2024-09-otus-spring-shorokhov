package ru.otus.hw.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.otus.hw.dao.CsvQuestionDao;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.service.IOService;
import ru.otus.hw.service.ResultService;
import ru.otus.hw.service.ResultServiceImpl;
import ru.otus.hw.service.StreamsIOService;
import ru.otus.hw.service.StudentService;
import ru.otus.hw.service.StudentServiceImpl;
import ru.otus.hw.service.TestRunnerService;
import ru.otus.hw.service.TestRunnerServiceImpl;
import ru.otus.hw.service.TestService;
import ru.otus.hw.service.TestServiceImpl;

import java.io.InputStream;
import java.io.PrintStream;

@Configuration
@Import(AppPropertiesConfig.class)
public class AppConfig {

    @Bean
    public TestRunnerService testRunnerService(
            TestService testService,
            StudentService studentService,
            ResultService resultService
    ) {
        return new TestRunnerServiceImpl(testService, studentService, resultService);
    }

    @Bean
    public TestService testService(IOService ioService, QuestionDao questionDao) {
        return new TestServiceImpl(ioService, questionDao);
    }

    @Bean
    public IOService ioService(
            @Value("#{T(System).out}") PrintStream printStream,
            @Value("#{T(System).in}") InputStream inputStream
    ) {
        return new StreamsIOService(printStream, inputStream);
    }

    @Bean
    public QuestionDao questionDao(TestFileNameProvider testFileNameProvider) {
        return new CsvQuestionDao(testFileNameProvider);
    }

    @Bean
    public StudentService studentService(IOService ioService) {
        return new StudentServiceImpl(ioService);
    }

    @Bean
    public ResultService resultService(TestConfig testConfig, IOService ioService) {
        return new ResultServiceImpl(testConfig, ioService);
    }
}
