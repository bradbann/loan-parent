package org.songbai.loan.config.converter;

import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;

import java.nio.charset.Charset;
import java.util.Arrays;

public class StringMessageConverter extends StringHttpMessageConverter {
	Charset UTF8 = Charset.forName("utf-8");
	public StringMessageConverter(){
		setWriteAcceptCharset(false);
		setSupportedMediaTypes(Arrays.asList(new MediaType("text", "html", UTF8)));
	}
}
