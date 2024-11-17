package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final IOService ioService;

    private final QuestionDao questionDao;

    @Override
    public TestResult executeTestFor(Student student) {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");
        var questions = questionDao.findAll();
        var testResult = new TestResult(student);

        for (var question : questions) {
            var isAnswerValid = requestAnswer(question); // Задать вопрос, получить ответ
            testResult.applyAnswer(question, isAnswerValid);
            ioService.printLine("");
        }
        return testResult;
    }

    private boolean requestAnswer(Question question) {
        int rightAnswer = -1;

        ioService.printLine(question.text());
        var answers = question.answers();
        for (int i = 1; i <= answers.size(); i++) {
            var answer = answers.get(i - 1);
            ioService.printFormattedLine("\t%s) %s", i, answer.text());
            if (answer.isCorrect()) {
                rightAnswer = i;
            }
        }

        int studentAnswer = ioService.readIntForRangeWithPrompt(1, answers.size(), ">", "There are no such options");
        return rightAnswer == studentAnswer;
    }
}
