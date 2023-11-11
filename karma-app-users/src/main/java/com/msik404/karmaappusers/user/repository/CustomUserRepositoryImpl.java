package com.msik404.karmaappusers.user.repository;

import java.util.*;

import com.mongodb.client.result.UpdateResult;
import com.msik404.karmaappusers.user.UserDocument;
import com.msik404.karmaappusers.user.dto.IdAndUsernameOnlyDto;
import com.msik404.karmaappusers.user.dto.UserUpdateDto;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.lang.NonNull;

@RequiredArgsConstructor
public class CustomUserRepositoryImpl implements CustomUserRepository {

    private final MongoOperations ops;

    @NonNull
    @Override
    public UpdateResult updateUser(@NonNull UserUpdateDto userUpdateDto) {

        var update = new Update();
        if (userUpdateDto.firstName() != null) {
            update.set("firstName", userUpdateDto.firstName());
        }
        if (userUpdateDto.lastName() != null) {
            update.set("lastName", userUpdateDto.lastName());
        }
        if (userUpdateDto.username() != null) {
            update.set("username", userUpdateDto.username());
        }
        if (userUpdateDto.email() != null) {
            update.set("email", userUpdateDto.email());
        }
        if (userUpdateDto.password() != null) {
            update.set("password", userUpdateDto.password());
        }
        if (userUpdateDto.role() != null) {
            update.set("role", userUpdateDto.role());
        }

        var query = new Query(Criteria.where("id").is(userUpdateDto.userId()));
        return ops.updateFirst(query, update, UserDocument.class);
    }

    @NonNull
    @Override
    public List<Optional<String>> findUsernames(@NonNull List<ObjectId> userIds) {

        assert !userIds.isEmpty();

        Map<ObjectId, Integer> userIdToIdx = new HashMap<>(userIds.size());
        for (int i = 0; i < userIds.size(); i++) {
            userIdToIdx.put(userIds.get(i), i);
        }

        var query = new Query(Criteria.where("_id").in(userIds));
        query.fields().include("_id", "username");

        List<IdAndUsernameOnlyDto> queryResults = ops.find(
                query,
                IdAndUsernameOnlyDto.class,
                ops.getCollectionName(UserDocument.class)
        );

        List<Optional<String>> results = new ArrayList<>(Collections.nCopies(userIds.size(), Optional.empty()));
        for (IdAndUsernameOnlyDto dto : queryResults) {
            results.set(userIdToIdx.get(dto.id()), Optional.of(dto.username()));
        }

        return results;
    }

}
