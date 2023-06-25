package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface RequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findAllByRequesterIdOrderByIdDesc(long requesterId);

    @Query("select r from ItemRequest r where r.requester.id <> ?1 order by r.id DESC")
    List<ItemRequest> findByRequesterNotOrderByIdDesc(long requesterId, Pageable pageable);

}