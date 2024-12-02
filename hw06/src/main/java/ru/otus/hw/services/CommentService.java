package ru.otus.hw.services;

import ru.otus.hw.repositories.entities.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentService {
    Optional<Comment> findById(long id);

    List<Comment> findAllByBookId(long id);

    Comment insert(String text, long bookId);

    Comment update(long id, String text);

    void deleteById(long id);
}
