package com.msik404.karmaappusers.grpc.impl.mapper;

import com.msik404.karmaappusers.grpc.UserRole;
import com.msik404.karmaappusers.grpc.impl.exception.UnsupportedRoleException;
import com.msik404.karmaappusers.user.Role;
import org.springframework.lang.NonNull;

public class RoleMapper {

    @NonNull
    public static Role map(@NonNull UserRole role) throws UnsupportedRoleException {

        return switch (role) {
            case ROLE_USER -> Role.USER;
            case ROLE_MOD -> Role.MOD;
            case ROLE_ADMIN -> Role.ADMIN;
            default -> throw new UnsupportedRoleException();
        };
    }

    @NonNull
    public static UserRole map(@NonNull Role role) {

        return switch (role) {
            case USER -> UserRole.ROLE_USER;
            case MOD -> UserRole.ROLE_MOD;
            case ADMIN -> UserRole.ROLE_ADMIN;
        };
    }

}
