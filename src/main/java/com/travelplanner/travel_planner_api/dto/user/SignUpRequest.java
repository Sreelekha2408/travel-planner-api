package com.travelplanner.travel_planner_api.dto.user;

import com.travelplanner.travel_planner_api.dto.ValidationPatterns;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

// Sign-up request, validated by @Valid in UserController. Uniqueness (email, username)
// needs the database, so UserService still checks that.
@Data
public class SignUpRequest {

  @Size(max = 255, message = "Username must be 255 characters or fewer")
  private String username;

  @NotBlank(message = "Email is required")
  @Email(regexp = ValidationPatterns.EMAIL, message = "Enter a valid email address")
  @Size(max = 255, message = "Email must be 255 characters or fewer")
  private String email;

  // BCrypt only uses the first 72 bytes of a password (and Spring Security rejects longer
  // ones with an error), so longer passwords are refused up front with a clear message.
  @NotNull(message = "Password is required")
  @Size(min = 8, message = "Use 8 or more characters with a mix of letters and numbers")
  @Size(max = 72, message = "Password must be 72 characters or fewer")
  @Pattern(regexp = ValidationPatterns.PASSWORD, message = "Use 8 or more characters with a mix of letters and numbers")
  private String password;

  @NotBlank(message = "First name is required")
  @Size(max = 255, message = "First name must be 255 characters or fewer")
  private String firstName;

  @Size(max = 255, message = "Last name must be 255 characters or fewer")
  private String lastName;

  @Size(max = 255, message = "Mobile must be 255 characters or fewer")
  private String mobile;

  @Size(max = 255, message = "Picture must be 255 characters or fewer")
  private String picture;
}
