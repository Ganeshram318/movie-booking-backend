package com.movie.backend.Service;

import com.movie.backend.dto.UserRequestDto;
import com.movie.backend.dto.UserResponseDto;

public interface UserService {
UserResponseDto register(UserRequestDto userRequestDto);
}
