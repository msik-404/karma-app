package com.msik404.karmaappusers.user.dto;

import org.bson.types.ObjectId;
import org.springframework.lang.NonNull;

public record IdOnlyDto(@NonNull ObjectId id) {
}
