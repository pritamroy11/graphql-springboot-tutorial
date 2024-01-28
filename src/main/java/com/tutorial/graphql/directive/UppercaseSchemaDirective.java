package com.tutorial.graphql.directive;

import graphql.language.StringValue;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetcherFactories;
import graphql.schema.FieldCoordinates;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLFieldsContainer;
import graphql.schema.idl.SchemaDirectiveWiring;
import graphql.schema.idl.SchemaDirectiveWiringEnvironment;

public class UppercaseSchemaDirective implements SchemaDirectiveWiring {

	@Override
	public GraphQLFieldDefinition onField(SchemaDirectiveWiringEnvironment<GraphQLFieldDefinition> environment) {
		GraphQLFieldDefinition field = environment.getElement();
		GraphQLFieldsContainer parentType = environment.getFieldsContainer();
		String conditionValue = ((StringValue) environment.getAppliedDirective().getArgument("condition")
				.getArgumentValue().getValue()).getValue();

		DataFetcher<?> existingDF = environment.getCodeRegistry()
				.getDataFetcher(FieldCoordinates.coordinates(parentType, field), field);
		DataFetcher<?> updatedDF = DataFetcherFactories.wrapDataFetcher(existingDF, ((datafetchingEnv, value) -> {
			if (value instanceof String stringValue && stringValue.contains(conditionValue)) {
				return stringValue.toUpperCase();
			}
			return value;
		}));

		environment.getCodeRegistry().dataFetcher(FieldCoordinates.coordinates(parentType, field), updatedDF);
		return field;
	}
}
