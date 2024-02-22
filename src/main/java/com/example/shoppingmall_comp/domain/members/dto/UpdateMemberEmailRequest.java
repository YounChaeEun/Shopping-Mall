package com.example.shoppingmall_comp.domain.members.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public record UpdateMemberEmailRequest(
        @NotBlank
        @Email
        String newEmail
) {
}
