package ru.practicum.shareit.booking.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.Status;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerIdOrderByBookingStartDesc(long bookerId, Pageable pageable);

    List<Booking> findAllByBookerIdAndBookingStartIsAfterOrderByBookingStartDesc(long bookerId,
                                                                                 LocalDateTime localDateTime, Pageable pageable);

    List<Booking> findAllByBookerIdAndBookingEndIsBeforeOrderByBookingStartDesc(long bookerId,
                                                                                LocalDateTime localDateTime, Pageable pageable);

    List<Booking> findAllByBookerIdAndStatusOrderByBookingStartDesc(long bookerId, Status status,
                                                                    Pageable pageable);

    @Query(value = "select b from Booking b where b.booker.id = ?1 and b.bookingStart < ?2 "
            + "and b.bookingEnd > ?2 order by b.bookingStart desc")
    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfter(long bookerId,
                                                             LocalDateTime localDateTime, Pageable pageable);

    @Query(value = "select b from Booking b where b.item.userId = ?1 order by b.bookingStart desc ")
    List<Booking> findAllByOwnerId(long ownerId, Pageable pageable);

    @Query(value = "select b from Booking b where b.item.userId = ?1 and b.bookingStart <= ?2"
            + " and b.bookingEnd >= ?2 order by b.bookingStart desc ")
    List<Booking> findAllByOwnerIdAndStartAfterAndEndBefore(long ownerId,
                                                            LocalDateTime localDateTime, Pageable pageable);

    @Query(value = "select b from Booking b where b.item.userId = ?1 and b.bookingStart > ?2 "
            + "order by b.bookingStart desc ")
    List<Booking> findAllByOwnerIdAndStartAfter(long ownerId, LocalDateTime localDateTime,
                                                Pageable pageable);

    @Query(value = "select b from Booking b where b.item.userId = ?1 and b.bookingEnd < ?2 "
            + "order by b.bookingStart desc ")
    List<Booking> findAllByOwnerIdAndEndBefore(long ownerId, LocalDateTime localDateTime,
                                               Pageable pageable);

    @Query(value = "select b from Booking b where b.item.userId = ?1 and b.status = ?2 "
            + "order by b.bookingStart desc ")
    List<Booking> findAllByOwnerIdAndState(long ownerId, Status status, Pageable pageable);

    @Query(value = "select b from Booking b where b.item.id = ?1 and b.bookingStart > ?2 "
        + "and b.status <> ?3 order by b.bookingStart asc")
    List<Booking> findAllByItemAndStateFuture(long itemId, LocalDateTime localDateTime,
        Status status);

    @Query(value =
        "select b from Booking b where b.item.id = ?1 "
            + "and ( b.bookingEnd < ?2 or b.bookingStart < ?2) "
            + "and b.status <> ?3 order by b.bookingEnd desc")
    List<Booking> findAllByItemAndStatePast(long itemId, LocalDateTime localDateTime,
        Status status);
}
