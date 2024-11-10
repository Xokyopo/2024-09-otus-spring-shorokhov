package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestServiceImplTest {

    private IOService ioService;
    private QuestionDao questionDao;
    private TestServiceImpl testService;
    private List<String> ioBuffer;

    @BeforeEach
    void setUp() {
        ioBuffer = new ArrayList<>();
        ioService = Mockito.mock(IOService.class);
        questionDao = Mockito.mock(QuestionDao.class);
        testService = new TestServiceImpl(ioService, questionDao);

        Mockito.doAnswer(a -> {
            Arrays.stream(a.getArguments())
                    .map(Object::toString)
                    .forEach(ioBuffer::add);
            return a;
        }).when(ioService).printFormattedLine(Mockito.any(), Mockito.any(Object[].class));

        Mockito.doAnswer(a -> {
            Arrays.stream(a.getArguments())
                    .map(Object::toString)
                    .forEach(ioBuffer::add);
            return a;
        }).when(ioService).printLine(Mockito.any());
    }

    @Test
    void executeTest_ShouldReturnTrue_WhenExecuteFindAllMethodFromQuestionDao() {
        Mockito.when(questionDao.findAll()).thenReturn(List.of(createTestQuestion()));

        testService.executeTest();

        Mockito.verify(questionDao, Mockito.atLeastOnce()).findAll();
    }

    @Test
    void executeTest_ShouldReturnTrue_WhenExecuteAnyMethodFromIoService() {
        Mockito.when(questionDao.findAll()).thenReturn(List.of(createTestQuestion()));

        testService.executeTest();

        assertFalse(ioBuffer.isEmpty());
    }

    @Test
    void executeTest_ShouldReturnTrue_WhenSenForPrintQuestion() {
        Question question = createTestQuestion();
        Mockito.when(questionDao.findAll()).thenReturn(List.of(question));

        testService.executeTest();

        String questionText = question.text();
        boolean questionPresent = ioBuffer.stream().anyMatch(line -> line.contains(questionText));

        assertTrue(questionPresent);
    }

    @Test
    void executeTest_ShouldReturnTrue_WhenSenForPrintAllAnswer() {
        Question question = createTestQuestion();
        Mockito.when(questionDao.findAll()).thenReturn(List.of(question));

        testService.executeTest();

        List<Answer> answers = question.answers();
        boolean allAnswerPresent = answers.stream()
                .map(Answer::text)
                .allMatch(answerText -> ioBuffer.stream().anyMatch(line -> line.contains(answerText)));

        assertTrue(allAnswerPresent);
    }

    private Question createTestQuestion() {
        List<Answer> answers = List.of(
                new Answer("first answer", false),
                new Answer("second answer", true)
        );
        return new Question("question?", answers);
    }
}
