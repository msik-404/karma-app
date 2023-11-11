package com.msik404.karmaappusers.grpc.impl;

import java.util.List;

import build.buf.protovalidate.ValidationResult;
import build.buf.protovalidate.Validator;
import build.buf.protovalidate.exceptions.ValidationException;
import com.google.protobuf.Empty;
import com.google.protobuf.Message;
import com.msik404.grpc.mongo.id.ProtoObjectId;
import com.msik404.karmaappusers.grpc.*;
import com.msik404.karmaappusers.grpc.impl.exception.FailedValidationException;
import com.msik404.karmaappusers.grpc.impl.exception.UnsupportedRoleException;
import com.msik404.karmaappusers.grpc.impl.mapper.GrpcToDocMapper;
import com.msik404.karmaappusers.grpc.impl.mapper.RoleMapper;
import com.msik404.karmaappusers.user.Role;
import com.msik404.karmaappusers.user.UserDocument;
import com.msik404.karmaappusers.user.UserService;
import com.msik404.karmaappusers.user.dto.IdAndHashedPasswordAndRoleOnlyDto;
import com.msik404.karmaappusers.user.dto.UserUpdateDto;
import com.msik404.karmaappusers.user.exception.DuplicateEmailException;
import com.msik404.karmaappusers.user.exception.DuplicateUnexpectedFieldException;
import com.msik404.karmaappusers.user.exception.DuplicateUsernameException;
import com.msik404.karmaappusers.user.exception.UserNotFoundException;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UsersGrpcImpl extends UsersGrpc.UsersImplBase {

    private final UserService service;

    private static <T> boolean validate(
            @NonNull Message request,
            @NonNull StreamObserver<T> responseObserver) {

        Validator validator = new Validator();
        try {
            ValidationResult result = validator.validate(request);
            // Check if there are any validation violations
            if (!result.isSuccess()) {
                var exception = new FailedValidationException(result.toString());
                responseObserver.onError(exception.asStatusRuntimeException());
                return false;
            }
        } catch (ValidationException ex) {
            // Catch and print any ValidationExceptions thrown during the validation process
            String errMessage = ex.getMessage();
            System.out.println("Validation failed: " + errMessage);
            responseObserver.onError(Status.INTERNAL
                    .withDescription(errMessage)
                    .asRuntimeException()
            );
            return false;
        }
        return true;
    }

    @Override
    public void createUser(
            CreateUserRequest request,
            StreamObserver<Empty> responseObserver) {

        boolean isSuccess = validate(request, responseObserver);
        if (!isSuccess) {
            return;
        }

        try {
            UserDocument doc = GrpcToDocMapper.map(request);
            service.save(doc);

            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();

        } catch (UnsupportedRoleException | DuplicateUsernameException | DuplicateEmailException |
                 DuplicateUnexpectedFieldException ex) {
            responseObserver.onError(ex.asStatusRuntimeException());
        }
    }

    @Override
    public void updateUser(
            UpdateUserRequest request,
            StreamObserver<Empty> responseObserver) {

        boolean isSuccess = validate(request, responseObserver);
        if (!isSuccess) {
            return;
        }

        try {
            UserUpdateDto dto = GrpcToDocMapper.map(request);
            service.update(dto);

            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();

        } catch (UserNotFoundException | UnsupportedRoleException | DuplicateUsernameException |
                 DuplicateEmailException |
                 DuplicateUnexpectedFieldException ex) {
            responseObserver.onError(ex.asStatusRuntimeException());
        }
    }

    @Override
    public void findCredentials(
            CredentialsRequest request,
            StreamObserver<CredentialsResponse> responseObserver) {

        boolean isSuccess = validate(request, responseObserver);
        if (!isSuccess) {
            return;
        }

        try {
            IdAndHashedPasswordAndRoleOnlyDto credentials = service.findCredentials(request.getEmail());

            var response = CredentialsResponse.newBuilder()
                    .setUserId(ProtoObjectId.newBuilder().setHexString(credentials.id().toHexString()).build())
                    .setPassword(credentials.password())
                    .setRole(RoleMapper.map(credentials.role()))
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (UserNotFoundException | UnsupportedRoleException ex) {
            responseObserver.onError(ex.asStatusRuntimeException());
        }
    }

    @Override
    public void findUserRole(
            ProtoObjectId request,
            StreamObserver<UserRoleResponse> responseObserver) {

        boolean isSuccess = validate(request, responseObserver);
        if (!isSuccess) {
            return;
        }

        try {
            Role role = service.findRole(new ObjectId(request.getHexString()));

            var response = UserRoleResponse.newBuilder()
                    .setRole(RoleMapper.map(role))
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (UserNotFoundException ex) {
            responseObserver.onError(ex.asStatusRuntimeException());
        }
    }

    @Override
    public void findUsername(
            ProtoObjectId request,
            StreamObserver<UsernameResponse> responseObserver) {

        try {
            String username = service.findUsername(new ObjectId(request.getHexString()));

            var response = UsernameResponse.newBuilder().setUsername(username).build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (UserNotFoundException ex) {
            responseObserver.onError(ex.asStatusRuntimeException());
        }
    }

    @Override
    public void findUsernames(
            UsernamesRequest request,
            StreamObserver<UsernamesResponse> responseObserver) {

        boolean isSuccess = validate(request, responseObserver);
        if (!isSuccess) {
            return;
        }

        List<ObjectId> userIds = request.getUserIdHexStringsList().stream()
                .map(ObjectId::new)
                .toList();

        List<String> usernames = service.findUsernames(userIds);

        var response = UsernamesResponse.newBuilder()
                .addAllUsernames(usernames).build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void findUserId(
            UserIdRequest request,
            StreamObserver<ProtoObjectId> responseObserver) {

        boolean isSuccess = validate(request, responseObserver);
        if (!isSuccess) {
            return;
        }

        try {
            ObjectId userId = service.findUserId(request.getUsername());

            var response = ProtoObjectId.newBuilder().setHexString(userId.toHexString()).build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (UserNotFoundException ex) {
            responseObserver.onError(ex.asStatusRuntimeException());
        }
    }

}
