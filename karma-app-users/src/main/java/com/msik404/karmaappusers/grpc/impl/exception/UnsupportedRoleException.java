package com.msik404.karmaappusers.grpc.impl.exception;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.springframework.lang.NonNull;

public class UnsupportedRoleException extends EncodableGrpcStatusException {

    private static final String Id = "UnsupportedRole";
    private static final String ERROR_MESSAGE = "Unsupported role provided.";

    public UnsupportedRoleException() {
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
        return Status.INVALID_ARGUMENT
                .withDescription(getEncodedException())
                .asRuntimeException();
    }

}
