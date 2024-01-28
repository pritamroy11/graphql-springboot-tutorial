package com.tutorial.graphql.utils;

import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

public class EncoderDecoder {
	
	private static final Encoder encoder = Base64.getEncoder();
	private static final Decoder decoder = Base64.getDecoder();
	
	public static String encode(String value) {
		return encoder.encodeToString(value.getBytes());
	}
	
	public static String decode(String encodedValue) {
		return new String(decoder.decode(encodedValue));
	}

}
