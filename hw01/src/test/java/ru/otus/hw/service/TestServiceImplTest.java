package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestServiceImplTest {

    private TestIoService ioService;
    private QuestionDao questionDao;
    private TestServiceImpl testService;

    @BeforeEach
    void setUp() {
        ioService = new TestIoService();
        questionDao = Mockito.mock(QuestionDao.class);
        testService = new TestServiceImpl(ioService, questionDao);
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

        assertFalse(ioService.ioBuffer.isEmpty());
    }

    @Test
    void executeTest_ShouldReturnTrue_WhenSenForPrintQuestion() {
        Question question = createTestQuestion();
        Mockito.when(questionDao.findAll()).thenReturn(List.of(question));

        testService.executeTest();

        String questionText = question.text();
        boolean questionPresent = ioService.ioBuffer.stream().anyMatch(line -> line.contains(questionText));

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
                .allMatch(answerText ->
                        ioService.ioBuffer.stream().anyMatch(line -> line.contains(answerText))
                );

        assertTrue(allAnswerPresent);
    }

    private Question createTestQuestion() {
        List<Answer> answers = List.of(
                new Answer("first answer", false),
                new Answer("second answer", true)
        );
        return new Question("question?", answers);
    }

    private static class TestIoService implements IOService {
        private final List<String> ioBuffer = new ArrayList<>();

        @Override
        public void printLine(String s) {
            ioBuffer.add(s);
        }

        @Override
        public void printFormattedLine(String s, Object... args) {
            ioBuffer.add(String.format(s, args));
        }

        public List<String> getIoBuffer() {
            return Collections.unmodifiableList(ioBuffer);
        }
    }
}
