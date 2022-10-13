package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    Collection<ItemRequest> findAllByRequestorOrderByCreatedDesc(User requestor);

    @Query("select ir from ItemRequest ir where ir.requestor.id <> ?1 order by ir.created desc")
    Page<ItemRequest> findAllWhereNotEqualRequestorId(Long requestorId, Pageable pageable);
}