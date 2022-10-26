package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Page<Booking> getAllByBookerOrderByStartDesc(User booker, Pageable pageable);

    @Query("select bkg " +
            "from Booking bkg " +
            "where bkg.booker = :booker " +
            "AND bkg.start < current_timestamp " +
            "AND bkg.end > current_timestamp " +
            "order by bkg.start desc")
    Page<Booking> getAllCurrentByBookerOrderByStartDesc(User booker, Pageable pageable);

    @Query("select bkg " +
            "from Booking bkg " +
            "where bkg.booker = :booker " +
            "AND bkg.end < current_timestamp " +
            "order by bkg.start desc")
    Page<Booking> getAllPastByBookerOrderByStartDesc(User booker, Pageable pageable);

    @Query("select bkg " +
            "from Booking bkg " +
            "where bkg.booker = :booker " +
            "AND bkg.start > current_timestamp " +
            "order by bkg.start desc")
    Page<Booking> getAllFutureByBookerOrderByStartDesc(User booker, Pageable pageable);

    Page<Booking> getAllByBookerAndStatusOrderByStartDesc(User booker, BookingStatus status, Pageable pageable);

    Page<Booking> getAllByItemOwnerOrderByStartDesc(User owner, Pageable pageable);

    @Query("select bkg " +
            "from Booking bkg " +
            "where bkg.item.owner = :owner " +
            "AND bkg.start < current_timestamp " +
            "AND bkg.end > current_timestamp " +
            "order by bkg.start desc")
    Page<Booking> getAllCurrentByItemOwnerOrderByStartDesc(User owner, Pageable pageable);

    @Query("select bkg " +
            "from Booking bkg " +
            "where bkg.item.owner = :owner " +
            "AND bkg.end < current_timestamp " +
            "order by bkg.start desc")
    Page<Booking> getAllPastByItemOwnerOrderByStartDesc(User owner, Pageable pageable);

    @Query("select bkg " +
            "from Booking bkg " +
            "where bkg.item.owner = :owner " +
            "AND bkg.start > current_timestamp " +
            "order by bkg.start desc")
    Page<Booking> getAllFutureByItemOwnerOrderByStartDesc(User owner, Pageable pageable);

    Page<Booking> getAllByItemOwnerAndStatusOrderByStartDesc(User owner, BookingStatus status, Pageable pageable);

    @Query("select bkg " +
            "from Booking bkg " +
            "where bkg.item = :item " +
            "AND bkg.start < current_timestamp " +
            "or bkg.end < current_timestamp " +
            "AND bkg.end > current_timestamp " +
            "order by bkg.start desc nulls first")
    Booking getCurrentOrPastBookingByItem(Item item);

    @Query("select bkg " +
            "from Booking bkg " +
            "where bkg.item = :item " +
            "AND bkg.start > current_timestamp " +
            "order by bkg.start asc nulls last ")
    Booking getFutureBookingByItem(Item item);
}