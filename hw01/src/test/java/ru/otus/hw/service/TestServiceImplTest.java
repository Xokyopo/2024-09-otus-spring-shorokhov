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
        this.ioService = new TestIoService();
        this.questionDao = Mockito.mock(QuestionDao.class);
        this.testService = new TestServiceImpl(this.ioService, this.questionDao);
    }

    @Test
    void executeTest_ShouldReturnTrue_WhenExecuteFindAllMethodFromQuestionDao() {
        Mockito.when(this.questionDao.findAll()).thenReturn(List.of(createTestQuestion()));

        this.testService.executeTest();

        Mockito.verify(this.questionDao, Mockito.atLeastOnce()).findAll();
    }

    @Test
    void executeTest_ShouldReturnTrue_WhenExecuteAnyMethodFromIoService() {
        Mockito.when(this.questionDao.findAll()).thenReturn(List.of(createTestQuestion()));

        this.testService.executeTest();

        assertFalse(this.ioService.ioBuffer.isEmpty());
    }

    @Test
    void executeTest_ShouldReturnTrue_WhenSenForPrintQuestion() {
        Question question = createTestQuestion();
        Mockito.when(this.questionDao.findAll()).thenReturn(List.of(question));

        this.testService.executeTest();

        String questionText = question.text();
        boolean questionPresent = this.ioService.ioBuffer.stream().anyMatch(line -> line.contains(questionText));

        assertTrue(questionPresent);
    }

    @Test
    void executeTest_ShouldReturnTrue_WhenSenForPrintAllAnswer() {
        Question question = createTestQuestion();
        Mockito.when(this.questionDao.findAll()).thenReturn(List.of(question));

        this.testService.executeTest();

        List<Answer> answers = question.answers();
        boolean allAnswerPresent = answers.stream()
                .map(Answer::text)
                .allMatch(answerText ->
                        this.ioService.ioBuffer.stream().anyMatch(line -> line.contains(answerText))
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
            this.ioBuffer.add(s);
        }

        @Override
        public void printFormattedLine(String s, Object... args) {
            this.ioBuffer.add(String.format(s, args));
        }

        public List<String> getIoBuffer() {
            return Collections.unmodifiableList(this.ioBuffer);
        }
    }
}