package uk.thisjowi.Password.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "password")
@Entity
public class Password {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String password;

    @JsonProperty("title")
    String name;

    @JsonProperty("website")
    String website;

    @JsonProperty("userId")
    Long userId;


}
