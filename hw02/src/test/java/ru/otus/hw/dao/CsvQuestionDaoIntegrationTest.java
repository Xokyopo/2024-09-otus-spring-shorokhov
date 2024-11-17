package ru.otus.hw.dao;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import ru.otus.hw.config.AppProperties;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;


class CsvQuestionDaoIntegrationTest {

    private static final String TEST_FILE_NAME = "/questions_for_CsvQuestionDaoIntegrationTest.csv";
    private static final int TEST_FILE_HEADER_SIZE = 0;

    private QuestionDao questionDao;

    @BeforeEach
    void setUp() {
        AppProperties fileNameProvider = new AppProperties();
        fileNameProvider.setTestFileHeaderSize(TEST_FILE_HEADER_SIZE);
        fileNameProvider.setTestFileName(TEST_FILE_NAME);
        questionDao = new CsvQuestionDao(fileNameProvider);
    }

    @Test
    void findAll_ShouldReturnNotEmptyCollection_WhenFileHaveQuestion() {
        Assertions.assertFalse(questionDao.findAll().isEmpty());
    }

    @Test
    void findAll_ShouldLoadAllQuestions() {
        List<Question> expected = getValidQuestions();
        List<Question> actual = questionDao.findAll();

        assertListEqualsWithoutOrder(expected, actual, Question::text);
    }

    @Test
    void findAll_ShouldLoadAllQuestionsAnswer() {
        List<Question> expectedQuestions = getValidQuestions();
        List<Question> actualQuestions = questionDao.findAll();

        for (Question actualQuestion : actualQuestions) {
            Question expectedQuestion = expectedQuestions.stream()
                    .filter(question -> Objects.equals(question.text(), actualQuestion.text()))
                    .findFirst()
                    .orElseGet(() -> Assertions.fail(String.format(
                            "Actual value [%s] not fount in expected list %s",
                            actualQuestion.text(),
                            Arrays.toString(expectedQuestions.stream().map(Question::text).toArray())
                    )));

            List<Answer> expectedAnswer = expectedQuestion.answers();
            List<Answer> actualAnswer = actualQuestion.answers();

            assertListEqualsWithoutOrder(expectedAnswer, actualAnswer, Answer::text);
            //Проверяем что буля обозначающая правильный ли ответ тоже загружена.
            assertListEqualsWithoutOrder(expectedAnswer, actualAnswer, answer -> {
                return String.format("[answer=%s, isCorrect=%s]", answer.text(), answer.isCorrect());
            });

        }
    }

    @Test
    void findAll_ShouldThrowException_WhenResourceNotFound() {
        String invalidFileName = "/file_not_exist.csv";
        AppProperties fileNameProvider = new AppProperties();
        fileNameProvider.setTestFileHeaderSize(TEST_FILE_HEADER_SIZE);
        fileNameProvider.setTestFileName(invalidFileName);
        questionDao = new CsvQuestionDao(fileNameProvider);

        Class<QuestionReadException> expected = QuestionReadException.class;
        Executable actual = questionDao::findAll;

        Assertions.assertThrows(expected, actual);
    }

    private <T> void assertListEqualsWithoutOrder(List<T> expected, List<T> actual, Function<T, Object> mapper) {
        List<Object> expectedValues = expected.stream()
                .map(mapper)
                .toList();
        List<Object> resultValues = new LinkedList<>(expectedValues);
        actual.stream()
                .map(mapper)
                .forEach(actualValue -> {
                    if (!resultValues.remove(actualValue)) {
                        Assertions.fail(String.format(
                                "Actual value [%s] not fount in expected list %s",
                                actualValue,
                                Arrays.toString(expectedValues.toArray())
                        ));
                    }
                });

        Assertions.assertTrue(resultValues.isEmpty());
    }

    private List<Question> getValidQuestions() {
        return List.of(
                new Question(
                        "What are not JSON value type?",
                        List.of(
                                new Answer("Object", false),
                                new Answer("Number", false),
                                new Answer("null", false),
                                new Answer("Enum", true)
                        )
                ),
                new Question(
                        "What are not web Framework?",
                        List.of(
                                new Answer("Spring", false),
                                new Answer("Hibernate", true),
                                new Answer("JakartaEE", false),
                                new Answer("Play", false)
                        )
                )
        );
    }
}
