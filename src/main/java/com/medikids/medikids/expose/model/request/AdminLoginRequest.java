package com.medikids.medikids.expose.model.request;

import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;

@Setter
@Getter
public class AdminLoginRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    private String email;
    private String password;
}
