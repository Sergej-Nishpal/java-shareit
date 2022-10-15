package ru.practicum.shareit.item.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Table(name = "items", schema = "public")
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "is_available", nullable = false)
    private Boolean available;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @ManyToOne
    @JoinColumn(name = "request_id")
    private ItemRequest itemRequest;

    @OneToMany(mappedBy = "item")
    @ToString.Exclude
    @JsonIgnore
    private Collection<Comment> comments = new ArrayList<>();

    @Override
    public boolean equals(Object item) {
        if (this == item) return true;
        if (!(item instanceof Item)) return false;
        return id != null && id.equals(((Item) item).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}