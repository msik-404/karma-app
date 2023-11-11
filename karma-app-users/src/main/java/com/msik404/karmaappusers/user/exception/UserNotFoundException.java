package com.msik404.karmaappusers.user.exception;

import com.msik404.karmaappusers.grpc.impl.exception.EncodableGrpcStatusException;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.springframework.lang.NonNull;

public class UserNotFoundException extends EncodableGrpcStatusException {

    private static final String Id = "UserNotFound";
    private static final String ERROR_MESSAGE = "Requested UserDocument was not found.";

    public UserNotFoundException() {
        super(ERROR_MESSAGE);
    }

    @NonNull
    @Override
    public String getExceptionId() {
        return Id;
    }

    @NonNull
    @Override
    public StatusRuntimeException asStatusRuntimeException() {
        return Status.NOT_FOUND
                .withDescription(getEncodedException())
                .asRuntimeException();
    }

}
