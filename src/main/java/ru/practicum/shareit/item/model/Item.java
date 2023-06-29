package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.model.ItemRequest;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "user_id", nullable = false)
    private long userId;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false, length = 1000)
    private String description;
    @Column(nullable = false)
    private Boolean available;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private ItemRequest request;
}
