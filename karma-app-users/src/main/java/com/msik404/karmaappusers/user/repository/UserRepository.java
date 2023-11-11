package com.msik404.karmaappusers.user.repository;

import java.util.Optional;

import com.msik404.karmaappusers.user.UserDocument;
import com.msik404.karmaappusers.user.dto.IdAndHashedPasswordAndRoleOnlyDto;
import com.msik404.karmaappusers.user.dto.IdOnlyDto;
import com.msik404.karmaappusers.user.dto.RoleOnlyDto;
import com.msik404.karmaappusers.user.dto.UsernameOnlyDto;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.lang.NonNull;

public interface UserRepository extends MongoRepository<UserDocument, ObjectId>, CustomUserRepository {

    @NonNull
    Optional<IdAndHashedPasswordAndRoleOnlyDto> findByEmail(@NonNull String email);

    @NonNull
    @Query("{ '_id' :  ?0 }")
    Optional<RoleOnlyDto> findRoleByUserId(@NonNull ObjectId userId);

    @NonNull
    @Query("{ '_id' :  ?0 }")
    Optional<UsernameOnlyDto> findUsernameByUserId(@NonNull ObjectId userId);

    @NonNull
    @Query("{ 'username' :  ?0 }")
    Optional<IdOnlyDto> findUserIdByUsername(@NonNull String username);

}
