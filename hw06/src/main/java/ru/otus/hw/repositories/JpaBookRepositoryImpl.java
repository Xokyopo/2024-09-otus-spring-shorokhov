package ru.otus.hw.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.otus.hw.repositories.entities.Book;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class JpaBookRepositoryImpl implements BookRepository {
    private static final String ENTITY_GRAPH_TYPE_LOAD = "jakarta.persistence.loadgraph";

    private static final String DEFAULT_ENTITY_GRAPH = "book-main-eg";

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<Book> findById(long id) {
        Object bookEntityGraph = em.getEntityGraph(DEFAULT_ENTITY_GRAPH);
        Map<String, Object> entityGraphInjection = Map.of(ENTITY_GRAPH_TYPE_LOAD, bookEntityGraph);

        return Optional.ofNullable(em.find(Book.class, id, entityGraphInjection));
    }

    @Override
    public List<Book> findAll() {
        Object bookEntityGraph = em.getEntityGraph(DEFAULT_ENTITY_GRAPH);

        return em.createQuery("FROM Book b", Book.class)
                .setHint(ENTITY_GRAPH_TYPE_LOAD, bookEntityGraph)
                .getResultList();
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == 0) {
            em.persist(book);
            return book;
        }

        return em.merge(book);
    }

    @Override
    public void deleteById(long id) {
        Optional.ofNullable(em.find(Book.class, id))
                .ifPresent(em::remove);
    }
}
