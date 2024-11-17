package ru.otus.hw.dao;

import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.dto.QuestionDto;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

@RequiredArgsConstructor
public class CsvQuestionDao implements QuestionDao {

    private static final char CSV_COLUMN_SEPARATOR = ';';

    private final TestFileNameProvider fileNameProvider;

    @Override
    public List<Question> findAll() {
        return loadQuestionFromCsv().stream()
                .map(QuestionDto::toDomainObject)
                .toList();
    }

    private List<QuestionDto> loadQuestionFromCsv() throws QuestionReadException {
        String questionFileName = fileNameProvider.getTestFileName();
        InputStream inputStream = getClass().getResourceAsStream(questionFileName);
        if (inputStream == null) {
            throw new QuestionReadException(String.format("Resource %s not fond", questionFileName));
        }

        try (Reader reader = new InputStreamReader(inputStream)) {
            return new CsvToBeanBuilder<QuestionDto>(reader)
                    .withType(QuestionDto.class)
                    .withSkipLines(fileNameProvider.getTestFileHeaderSize())
                    .withSeparator(CSV_COLUMN_SEPARATOR)
                    .build()
                    .parse();
        } catch (IOException e) {
            throw new QuestionReadException(e.getMessage(), e);
        }
    }
}
