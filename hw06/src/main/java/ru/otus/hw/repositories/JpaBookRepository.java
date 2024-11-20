package ru.otus.hw.repositories;

import jakarta.persistence.EntityGraph;
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
public class JpaBookRepository implements BookRepository {
    private static final String ENTITY_GRAPH_TYPE_LOAD = "jakarta.persistence.loadgraph";

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<Book> findById(long id) {
        EntityGraph<Book> bookEntityGraph = em.createEntityGraph(Book.class);
        Map<String, Object> entityGraphInjection = Map.of(ENTITY_GRAPH_TYPE_LOAD, bookEntityGraph);

        return Optional.ofNullable(em.find(Book.class, id, entityGraphInjection));
    }

    @Override
    public List<Book> findAll() {
        EntityGraph<Book> bookEntityGraph = em.createEntityGraph(Book.class);

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
