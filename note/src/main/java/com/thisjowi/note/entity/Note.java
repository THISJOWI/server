package com.thisjowi.note.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;



@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "notes")
public class Note {

   @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long Id;

   @Column(columnDefinition = "TEXT")
   private String content;

   @Column(unique = true, nullable = false)
   private String title;

   @DateTimeFormat
   private LocalDateTime createdAt;

   private Long userId;

}
