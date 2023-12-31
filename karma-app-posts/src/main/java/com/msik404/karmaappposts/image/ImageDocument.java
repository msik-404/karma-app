package com.msik404.karmaappposts.image;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.Binary;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "images")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ImageDocument {

    @Id
    private ObjectId postId;

    private Binary imageData;

}
