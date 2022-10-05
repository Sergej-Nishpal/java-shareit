package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Booking getBookingById(Long id);

    Collection<Booking> getAllByBookerOrderByStartDesc(User booker);

    @Query("select bkg from Booking bkg where bkg.booker = :booker AND " +
            "bkg.start < current_timestamp AND bkg.end > current_timestamp " +
            "order by bkg.start desc")
    Collection<Booking> getAllCurrentByBookerOrderByStartDesc(User booker);

    @Query("select bkg from Booking bkg where bkg.booker = :booker AND " +
            "bkg.end < current_timestamp " +
            "order by bkg.start desc")
    Collection<Booking> getAllPastByBookerOrderByStartDesc(User booker);

    @Query("select bkg from Booking bkg where bkg.booker = :booker AND " +
            "bkg.start > current_timestamp " +
            "order by bkg.start desc")
    Collection<Booking> getAllFutureByBookerOrderByStartDesc(User booker);

    Collection<Booking> getAllByBookerAndStatusOrderByStartDesc(User booker, BookingStatus status);

    Collection<Booking> getAllByItemOwnerOrderByStartDesc(User owner);

    @Query("select bkg from Booking bkg where bkg.item.owner = :owner AND " +
            "bkg.start < current_timestamp AND bkg.end > current_timestamp " +
            "order by bkg.start desc")
    Collection<Booking> getAllCurrentByItemOwnerOrderByStartDesc(User owner);

    @Query("select bkg from Booking bkg where bkg.item.owner = :owner AND " +
            "bkg.end < current_timestamp " +
            "order by bkg.start desc")
    Collection<Booking> getAllPastByItemOwnerOrderByStartDesc(User owner);

    @Query("select bkg from Booking bkg where bkg.item.owner = :owner AND " +
            "bkg.start > current_timestamp " +
            "order by bkg.start desc")
    Collection<Booking> getAllFutureByItemOwnerOrderByStartDesc(User owner);

    Collection<Booking> getAllByItemOwnerAndStatusOrderByStartDesc(User owner, BookingStatus status);
}