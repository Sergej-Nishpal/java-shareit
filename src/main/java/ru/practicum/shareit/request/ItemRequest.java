package ru.practicum.shareit.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Table(name = "requests")
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "created_at", nullable = false)
    @DateTimeFormat(pattern = "YYYY-MM-DD'T'HH:mm:ss")
    private LocalDateTime created;

    @ManyToOne
    @JoinColumn(name = "requestor_id", nullable = false)
    private User requestor;

    @OneToMany(mappedBy = "itemRequest")
    @ToString.Exclude
    @JsonIgnore
    private Collection<Item> items = new ArrayList<>();

    @Override
    public boolean equals(Object itemRequest) {
        if (this == itemRequest) return true;
        if (!(itemRequest instanceof ItemRequest)) return false;
        return id != null && id.equals(((ItemRequest) itemRequest).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}