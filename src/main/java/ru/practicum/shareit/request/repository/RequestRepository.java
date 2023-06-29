package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.ItemRequest;

public interface RequestRepository extends JpaRepository<ItemRequest, Long> {
    Page<ItemRequest> findAllByRequesterIdOrderByIdDesc(long requesterId, Pageable pageable);

    @Query("select r from ItemRequest r where r.requester.id <> ?1 order by r.id DESC")
    Page<ItemRequest> findByRequesterNotOrderByIdDesc(long requesterId, Pageable pageable);
}