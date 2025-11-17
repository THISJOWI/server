package uk.thisjowi.Password.Entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * PasswordDTO - Data Transfer Object for Password.
 * Used in API requests. Validation is done in the controller.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordDTO {

    String password;

    @JsonProperty("title")
    String name;

    String username;

    @JsonProperty("website")
    String website;

    /**
     * Convert DTO to Entity
     */
    public Password toEntity() {
        Password password = new Password();
        password.setPassword(this.password);
        password.setName(this.name);
        password.setUsername(this.username);
        password.setWebsite(this.website);
        return password;
    }
}
