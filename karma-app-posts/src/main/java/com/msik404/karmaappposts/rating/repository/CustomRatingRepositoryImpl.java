package com.msik404.karmaappposts.rating.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.msik404.karmaappposts.post.PostDocument;
import com.msik404.karmaappposts.post.Visibility;
import com.msik404.karmaappposts.post.order.PostDocRetrievalOrderStrategy;
import com.msik404.karmaappposts.post.position.PostDocScrollPositionConcrete;
import com.msik404.karmaappposts.post.repository.criteria.PostDocScrollingCriteria;
import com.msik404.karmaappposts.rating.RatingDocument;
import com.msik404.karmaappposts.rating.dto.IdAndIsPositiveOnlyDto;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.aggregation.ConditionalOperators.Cond;
import org.springframework.data.mongodb.core.aggregation.VariableOperators.Let;
import org.springframework.data.mongodb.core.aggregation.VariableOperators.Let.ExpressionVariable;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

@RequiredArgsConstructor
public class CustomRatingRepositoryImpl implements CustomRatingRepository {

    private final MongoOperations ops;

    @NonNull
    private List<IdAndIsPositiveOnlyDto> findFirstNImpl(
            int size,
            @Nullable ObjectId creatorId,
            @NonNull ObjectId clientId,
            @NonNull PostDocScrollPositionConcrete position,
            @NonNull Collection<Visibility> visibilities,
            @NonNull PostDocRetrievalOrderStrategy order) {

        assert !visibilities.isEmpty();

        List<AggregationOperation> aggOps = new ArrayList<>();

        // matching - query
        var matchCriteria = Criteria.where("visibility").in(visibilities);

        List<Criteria> additionalCriteria = new ArrayList<>();

        if (!position.isInitial()) {
            additionalCriteria.add(
                    PostDocScrollingCriteria.getNonInitial(position.karmaScore(), position.postId()));
        }

        if (creatorId != null) {
            additionalCriteria.add(Criteria.where("userId").is(creatorId));
        }

        if (!additionalCriteria.isEmpty()) {
            matchCriteria.andOperator(additionalCriteria);
        }

        aggOps.add(new MatchOperation(matchCriteria));

        // limit results
        aggOps.add(new LimitOperation(size));

        // sort results
        aggOps.add(new SortOperation(order.getOrderStrategy()));

        // lookup - outer left join
        var postIdVar = ExpressionVariable.newVariable("postId").forField("_id");

        Bson bsonQuery = new Document("$match",
                new Document("$expr",
                        new Document("$and", List.of(
                                new Document("$eq", Arrays.asList("$postId", "$$postId")),
                                new Document("$eq", Arrays.asList("$userId", clientId))
                        ))
                )
        );

        AggregationOperation multiFieldLeftOuterJoinOperation = Aggregation.stage(bsonQuery);

        aggOps.add(new LookupOperation(
                ops.getCollectionName(RatingDocument.class),
                Let.just(postIdVar),
                Aggregation.newAggregation(multiFieldLeftOuterJoinOperation).getPipeline(),
                Fields.field("rating_docs")
        ));

        // projection
        AggregationExpression arrayIsNotEmpty = ComparisonOperators.Gt.valueOf(
                        ArrayOperators.Size.lengthOfArray("rating_docs.isPositive"))
                .greaterThanValue(0);

        AggregationExpression getFirstEl = ArrayOperators.arrayOf("rating_docs.isPositive")
                .elementAt(0);

        var projectionOperation = Aggregation.project("_id").and("rating_docs.isPositive")
                .applyCondition(Cond.when(arrayIsNotEmpty)
                        .thenValueOf(getFirstEl)
                        .otherwise("$$REMOVE"));

        aggOps.add(projectionOperation);

        // build aggregation query
        TypedAggregation<PostDocument> agg = Aggregation.newAggregation(PostDocument.class, aggOps);

        // run query
        AggregationResults<IdAndIsPositiveOnlyDto> results = ops.aggregate(agg, IdAndIsPositiveOnlyDto.class);

        return results.getMappedResults();
    }

    @NonNull
    @Override
    public List<IdAndIsPositiveOnlyDto> findFirstN(
            int size,
            @NonNull ObjectId clientId,
            @NonNull PostDocScrollPositionConcrete position,
            @NonNull Collection<Visibility> visibilities,
            @NonNull PostDocRetrievalOrderStrategy order) {

        return findFirstNImpl(size, null, clientId, position, visibilities, order);
    }

    @NonNull
    @Override
    public List<IdAndIsPositiveOnlyDto> findFirstN(
            int size,
            @NonNull ObjectId creatorId,
            @NonNull ObjectId clientId,
            @NonNull PostDocScrollPositionConcrete position,
            @NonNull Collection<Visibility> visibilities,
            @NonNull PostDocRetrievalOrderStrategy order) {

        return findFirstNImpl(size, creatorId, clientId, position, visibilities, order);
    }

}
