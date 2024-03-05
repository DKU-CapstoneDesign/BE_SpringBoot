package proejct.capstone.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import proejct.capstone.domain.RefreshToken;
import proejct.capstone.repository.RefreshTokenRepository;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken findByRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected token"));
    }
}