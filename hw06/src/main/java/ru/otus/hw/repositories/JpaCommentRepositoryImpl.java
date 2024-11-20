package ru.otus.hw.repositories;

import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import ru.otus.hw.repositories.entities.Comment;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class JpaCommentRepositoryImpl implements CommentRepository {
    private static final String ENTITY_GRAPH_TYPE_LOAD = "jakarta.persistence.loadgraph";

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<Comment> findById(long id) {
        EntityGraph<Comment> commentEntityGraph = em.createEntityGraph(Comment.class);
        Map<String, Object> entityGraphInjection = Map.of(ENTITY_GRAPH_TYPE_LOAD, commentEntityGraph);

        return Optional.ofNullable(em.find(Comment.class, id, entityGraphInjection));
    }

    @Override
    public List<Comment> findAllByBookId(long bookId) {
        EntityGraph<Comment> commentEntityGraph = em.createEntityGraph(Comment.class);

        return em.createQuery("FROM Comment c WHERE c.source.id = :bookId", Comment.class)
                .setParameter("bookId", bookId)
                .setHint(ENTITY_GRAPH_TYPE_LOAD, commentEntityGraph)
                .getResultList();
    }

    @Override
    public Comment save(Comment comment) {
        if (comment.getId() == 0) {
            em.persist(comment);
            return comment;
        }

        return em.merge(comment);
    }

    @Override
    public void deleteById(long id) {
        Optional.ofNullable(em.getReference(Comment.class, id))
                .ifPresent(em::remove);
    }
}
