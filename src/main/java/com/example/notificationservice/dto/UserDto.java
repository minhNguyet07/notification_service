package com.example.notificationservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserDto {
    @NotBlank(message = "Email không được để trống")
    private String email;

    @NotBlank(message = "Message không được để trống")
    @Size(max = 500, message = "Message không được vượt quá 500 ký tự")
    private String message;

    public UserDto() {
    }

    public UserDto(String email, String message) {
        this.email = email;
        this.message = message;
    }

}
