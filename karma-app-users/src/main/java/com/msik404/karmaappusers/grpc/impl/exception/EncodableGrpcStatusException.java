package com.msik404.karmaappusers.grpc.impl.exception;

import com.msik404.karmaappusers.encoding.EncodableException;
import com.msik404.karmaappusers.encoding.ExceptionEncoder;
import org.springframework.lang.NonNull;

public abstract class EncodableGrpcStatusException extends RuntimeException implements EncodableException, GrpcStatusException {

    public EncodableGrpcStatusException(@NonNull String errorMessage) {
        super(errorMessage);
    }

    @NonNull
    @Override
    public String getEncodedException() {
        return ExceptionEncoder.encode(getExceptionId(), getMessage());
    }

}
