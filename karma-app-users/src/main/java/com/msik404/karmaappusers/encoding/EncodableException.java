package com.msik404.karmaappusers.encoding;

import org.springframework.lang.NonNull;

public interface EncodableException {

    @NonNull
    String getEncodedException();

}
