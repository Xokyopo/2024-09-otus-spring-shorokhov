package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestRunnerServiceImplTest {

    private TestService testService;

    private StudentService studentService;

    private ResultService resultService;

    private TestRunnerService service;

    @BeforeEach
    void setUp() {
        testService = Mockito.mock(TestService.class);
        studentService = Mockito.mock(StudentService.class);
        resultService = Mockito.mock(ResultService.class);
        service = new TestRunnerServiceImpl(testService, studentService, resultService);
    }

    @Test
    void run_ShouldTransferStudentToTestService_WhenGetStudentFromStudentService() {
        Student expected = new Student("fakeName", "fakeSoname");

        Mockito.when(studentService.determineCurrentStudent()).thenReturn(expected);
        ArgumentCaptor<Student> argumentCaptor = ArgumentCaptor.forClass(Student.class);
        Mockito.when(testService.executeTestFor(argumentCaptor.capture())).thenReturn(null);

        service.run();

        Student actual = argumentCaptor.getValue();

        assertEquals(expected, actual);
    }

    @Test
    void run_ShouldTransferTestResultToResultService_WhenGetTestResultFromTestService() {
        Student fakeStudent = new Student("fakeName", "fakeSoname");
        TestResult expected = new TestResult(fakeStudent);

        Mockito.when(testService.executeTestFor(Mockito.any())).thenReturn(expected);
        ArgumentCaptor<TestResult> argumentCaptor = ArgumentCaptor.forClass(TestResult.class);
        Mockito.doNothing().when(resultService).showResult(argumentCaptor.capture());

        service.run();

        TestResult actual = argumentCaptor.getValue();

        assertEquals(expected, actual);
    }
}
