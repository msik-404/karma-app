package com.msik404.karmaappposts.post.position;

import org.bson.types.ObjectId;
import org.springframework.lang.NonNull;

public class PostDocScrollPosition {

    @NonNull
    public static PostDocScrollPositionConcrete initial() {
        return new PostDocScrollPositionConcrete();
    }

    @NonNull
    public static PostDocScrollPositionConcrete of(long karmaScore, @NonNull ObjectId postId) {
        return new PostDocScrollPositionConcrete(karmaScore, postId);
    }

}
