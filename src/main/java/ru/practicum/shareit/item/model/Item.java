package ru.practicum.shareit.item.model;

import lombok.*;

import javax.persistence.*;

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

    @JoinColumn(name = "owner_id")
    private Long ownerId;

    @JoinColumn(name = "request_id")
    private Long requestId;

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