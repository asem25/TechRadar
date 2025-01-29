package ru.semavin.TechRadarPolls.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String accessToken;
    private String refreshToken;

    @Override
    public String toString() {
        return String.format("""
                AccessToken: {%s},
                RefreshToken: {%s}
                """, accessToken, refreshToken);
    }
}