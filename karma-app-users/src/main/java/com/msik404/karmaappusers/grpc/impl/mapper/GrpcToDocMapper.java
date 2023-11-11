package com.msik404.karmaappusers.grpc.impl.mapper;

import com.msik404.karmaappusers.grpc.CreateUserRequest;
import com.msik404.karmaappusers.grpc.UpdateUserRequest;
import com.msik404.karmaappusers.grpc.impl.exception.UnsupportedRoleException;
import com.msik404.karmaappusers.user.UserDocument;
import com.msik404.karmaappusers.user.dto.UserUpdateDto;
import org.bson.types.ObjectId;
import org.springframework.lang.NonNull;

public class GrpcToDocMapper {

    @NonNull
    public static UserDocument map(@NonNull CreateUserRequest request) throws UnsupportedRoleException {

        return new UserDocument(
                request.hasFirstName() ? request.getFirstName() : null,
                request.hasLastName() ? request.getLastName() : null,
                request.getUsername(),
                request.getEmail(),
                request.getPassword(),
                RoleMapper.map(request.getRole())
        );
    }

    @NonNull
    public static UserUpdateDto map(@NonNull UpdateUserRequest request) throws UnsupportedRoleException {

        return new UserUpdateDto(
                new ObjectId(request.getUserId().getHexString()),
                request.hasFirstName() ? request.getFirstName() : null,
                request.hasLastName() ? request.getLastName() : null,
                request.hasUsername() ? request.getUsername() : null,
                request.hasEmail() ? request.getEmail() : null,
                request.hasPassword() ? request.getPassword() : null,
                request.hasRole() ? RoleMapper.map(request.getRole()) : null
        );
    }

}
