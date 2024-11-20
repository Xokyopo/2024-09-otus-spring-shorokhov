package ru.otus.hw.repositories.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedSubgraph;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NamedEntityGraph(
        attributeNodes = {
                @NamedAttributeNode(value = "source", subgraph = "source-subgraph")
        },
        subgraphs = {
                @NamedSubgraph(
                        name = "source-subgraph",
                        attributeNodes = {
                                @NamedAttributeNode("author"),
                                @NamedAttributeNode("genre"),
                        }
                )
        }
)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString(of = {"id", "text"})
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JoinColumn(name = "id")
    private long id;

    @ManyToOne
    @JoinColumn(name = "source_id")
    private Book source;

    @JoinColumn(name = "text")
    @Column(length = 1000)
    private String text;
}
