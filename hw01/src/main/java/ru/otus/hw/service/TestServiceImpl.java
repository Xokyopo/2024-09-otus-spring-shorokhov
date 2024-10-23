package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.List;

@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final IOService ioService;

    private final QuestionDao questionDao;

    @Override
    public void executeTest() {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");
        // Получить вопросы из дао и вывести их с вариантами ответов

        List<Question> questions = this.questionDao.findAll();
        this.printQuestions(questions);
    }

    private void printQuestions(List<Question> questions) {
        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            this.ioService.printFormattedLine("%s %s%n", i + 1, question.text());

            List<Answer> questionAnswers = question.answers();
            for (int c = 0; c < questionAnswers.size(); c++) {
                this.ioService.printFormattedLine("\t%s) %s%n", c + 1, questionAnswers.get(c));
            }
        }
    }
}
