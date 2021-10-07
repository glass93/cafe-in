package com.cafein.entity;

import lombok.*;
import org.hibernate.annotations.*;
import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import static javax.persistence.GenerationType.*;

@DynamicInsert
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter @Setter
@Entity
@Table(name = "menu")
public class Menu {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name="cafe_id", nullable=false)    
    private Cafe cafe;
    
    @Column(name = "name", nullable=false, length = 126)
    private String name;

    @Column(name = "price", nullable=false)
    private int price;

}
