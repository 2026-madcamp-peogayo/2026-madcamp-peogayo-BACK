package com.example.madcamp.peogayo.Back.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SignupRequest {
    private String loginId;
    private String password;
    private String nickname;
    private String passwordCheck;
}