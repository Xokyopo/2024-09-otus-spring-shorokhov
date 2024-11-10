package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final IOService ioService;

    private final QuestionDao questionDao;

    @Override
    public void executeTest() {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");
        // Получить вопросы из дао и вывести их с вариантами ответов

        try {
            List<Question> questions = Optional.of(questionDao)
                    .map(QuestionDao::findAll)
                    .filter(list -> !list.isEmpty())
                    .orElseThrow(() -> new QuestionReadException("Question not found"));
            printQuestions(questions);
        } catch (QuestionReadException e) {
            ioService.printLine("Error during get and print questions");
        }
    }

    private void printQuestions(List<Question> questions) {
        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            ioService.printFormattedLine("%s. %s", i + 1, question.text());

            List<Answer> questionAnswers = question.answers();
            if (questionAnswers == null) {
                throw new QuestionReadException(String.format("Answers not found for question [%s]", question.text()));
            }
            for (int c = 0; c < questionAnswers.size(); c++) {
                ioService.printFormattedLine("\t%s) %s", c + 1, questionAnswers.get(c).text());
            }
            ioService.printLine("");
        }
    }
}
