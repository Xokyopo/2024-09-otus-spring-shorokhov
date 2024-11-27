package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.converters.CommentConverter;
import ru.otus.hw.services.CommentService;

import java.util.stream.Collectors;

@ShellComponent
@RequiredArgsConstructor
public class CommentCommands {
    private static final String LINE_DELIMITER = ",%s".formatted(System.lineSeparator());

    private final CommentService service;

    private final CommentConverter converter;

    @ShellMethod(key = "cbid", value = "Find comment by id")
    public String findById(long id) {
        return service.findById(id)
                .map(converter::commentToStringFull)
                .orElse("Comment with id %d not found".formatted(id));
    }

    @ShellMethod(key = "acbbid", value = "Find all comment by book id")
    public String findAllByBookId(long bookId) {
        return service.findAllByBookId(bookId).stream()
                .map(converter::commentToString)
                .collect(Collectors.joining(LINE_DELIMITER));
    }

    @ShellMethod(value = "Insert comment", key = "cins")
    public String insert(long bookId, String comment) {
        var savedComment = service.insert(comment, bookId);
        return converter.commentToStringFull(savedComment);
    }

    @ShellMethod(value = "Update comment", key = "cupd")
    public String update(long id, String comment) {
        var savedComment = service.update(id, comment);
        return converter.commentToStringFull(savedComment);
    }

    @ShellMethod(value = "Delete comment by id", key = "cdel")
    public void delete(long id) {
        service.deleteById(id);
    }
}
