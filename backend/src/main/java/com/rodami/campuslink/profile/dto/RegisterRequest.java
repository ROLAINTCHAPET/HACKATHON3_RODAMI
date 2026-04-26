package com.rodami.campuslink.profile.dto;

public record RegisterRequest(
    String nom,
    String prenom,
    String email,
    String password
) {}
