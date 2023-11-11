package com.msik404.karmaappusers.user.repository;

import java.util.List;
import java.util.Optional;

import com.mongodb.client.result.UpdateResult;
import com.msik404.karmaappusers.user.dto.UserUpdateDto;
import org.bson.types.ObjectId;
import org.springframework.lang.NonNull;

public interface CustomUserRepository {

    @NonNull
    UpdateResult updateUser(@NonNull UserUpdateDto userUpdateDto);

    @NonNull
    List<Optional<String>> findUsernames(@NonNull List<ObjectId> userIds);

}
