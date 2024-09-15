package project.capstone.common;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ApiResponseDto<T> {

    private boolean success;
    private T response;
    private ErrorResponse error;

    @Builder
    private ApiResponseDto(boolean success, T response, ErrorResponse error) {
        this.success = success;
        this.response = response;
        this.error = error;
    }
    // 성공 응답을 생성하는 메서드
    public static ApiResponseDto<String> success(String message) {
        return ApiResponseDto.<String>builder()
                .success(true)
                .response(message)
                .build();
    }
}