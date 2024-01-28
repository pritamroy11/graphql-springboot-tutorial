package com.tutorial.graphql;

import java.util.regex.Pattern;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

import com.tutorial.graphql.directive.UppercaseSchemaDirective;

import graphql.Scalars;
import graphql.scalars.ExtendedScalars;
import graphql.schema.GraphQLScalarType;

@Configuration
public class GraphQLConfig {
	
	GraphQLScalarType emailCustomScalar = ExtendedScalars
			.newRegexScalar("Email")
			.description("Custom Scalar for Email Address")
			.addPattern(Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$"))
			.build();
	
	GraphQLScalarType keyAliasScalar = ExtendedScalars
			.newAliasedScalar("Key")
			.description("Key for ID scalar")
			.aliasedScalar(Scalars.GraphQLID)
			.build();
	
	GraphQLScalarType CursorStringScalar = ExtendedScalars
			.newAliasedScalar("CursorString")
			.description("Cursor scalar type")
			.aliasedScalar(Scalars.GraphQLID)
			.build();

	@Bean
	public RuntimeWiringConfigurer runtimeWiringConfigurer () {
		return wiringBuilder -> wiringBuilder
				.scalar(ExtendedScalars.GraphQLBigDecimal)
				.scalar(emailCustomScalar)
				.scalar(keyAliasScalar)
				.scalar(CursorStringScalar)
				.directive("uppercase", new UppercaseSchemaDirective())
				.build();
	}
}
