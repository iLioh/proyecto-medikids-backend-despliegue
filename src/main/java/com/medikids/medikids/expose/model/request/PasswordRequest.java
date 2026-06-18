package com.medikids.medikids.expose.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordRequest {
    private String currentPassword;
    private String newPassword;
}
