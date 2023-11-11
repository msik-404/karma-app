package com.msik404.karmaappusers.user.dto;

import com.msik404.karmaappusers.user.Role;
import org.bson.types.ObjectId;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public record UserUpdateDto(@NonNull ObjectId userId, @Nullable String firstName, @Nullable String lastName,
                            @Nullable String username, @Nullable String email, @Nullable String password,
                            @Nullable Role role) {
}
