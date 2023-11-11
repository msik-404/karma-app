package com.msik404.karmaappusers.user;

import java.util.List;
import java.util.Optional;

import com.mongodb.client.result.UpdateResult;
import com.msik404.karmaappusers.user.dto.*;
import com.msik404.karmaappusers.user.exception.DuplicateEmailException;
import com.msik404.karmaappusers.user.exception.DuplicateUnexpectedFieldException;
import com.msik404.karmaappusers.user.exception.DuplicateUsernameException;
import com.msik404.karmaappusers.user.exception.UserNotFoundException;
import com.msik404.karmaappusers.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    public void save(@NonNull UserDocument userDocument)
            throws DuplicateUsernameException, DuplicateEmailException, DuplicateUnexpectedFieldException {

        try {
            repository.save(userDocument);
        } catch (DuplicateKeyException ex) {
            DuplicateKeyExceptionHandler.handle(ex);
        }
    }

    public void update(@NonNull UserUpdateDto userUpdateDto)
            throws UserNotFoundException, DuplicateUsernameException, DuplicateEmailException, DuplicateUnexpectedFieldException {

        try {
            UpdateResult result = repository.updateUser(userUpdateDto);
            if (result.getMatchedCount() == 0) {
                throw new UserNotFoundException();
            }
        } catch (DuplicateKeyException ex) {
            DuplicateKeyExceptionHandler.handle(ex);
        }
    }

    @NonNull
    public IdAndHashedPasswordAndRoleOnlyDto findCredentials(
            @NonNull String email) throws UserNotFoundException {

        return repository.findByEmail(email).orElseThrow(UserNotFoundException::new);
    }

    @NonNull
    public Role findRole(@NonNull ObjectId userId) throws UserNotFoundException {

        Optional<RoleOnlyDto> optionalRoleDto = repository.findRoleByUserId(userId);
        if (optionalRoleDto.isEmpty()) {
            throw new UserNotFoundException();
        }
        return optionalRoleDto.get().role();
    }

    @NonNull
    public String findUsername(@NonNull ObjectId userId) throws UserNotFoundException {

        Optional<UsernameOnlyDto> optionalUsernameDto = repository.findUsernameByUserId(userId);
        if (optionalUsernameDto.isEmpty()) {
            throw new UserNotFoundException();
        }
        return optionalUsernameDto.get().username();
    }

    @NonNull
    public List<String> findUsernames(@NonNull List<ObjectId> userIds) {

        List<Optional<String>> optionalUsernames = repository.findUsernames(userIds);

        return optionalUsernames.stream().map(optional -> optional.orElse("")).toList();
    }

    @NonNull
    public ObjectId findUserId(@NonNull String username) throws UserNotFoundException {

        Optional<IdOnlyDto> optionalIdDto = repository.findUserIdByUsername(username);
        if (optionalIdDto.isEmpty()) {
            throw new UserNotFoundException();
        }
        return optionalIdDto.get().id();
    }

}
