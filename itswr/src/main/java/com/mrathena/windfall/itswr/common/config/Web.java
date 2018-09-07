package com.mrathena.windfall.itswr.common.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;

@Configuration
public class Web implements WebMvcConfigurer {

	@Bean("StringHttpMessageConverter")
	public StringHttpMessageConverter getStringHttpMessageConverter() {
		StringHttpMessageConverter converter = new StringHttpMessageConverter();
		List<MediaType> mediaTypes = new ArrayList<>();
		mediaTypes.add(MediaType.TEXT_PLAIN);
		converter.setSupportedMediaTypes(mediaTypes);
		return converter;
	}

	@Bean("JsonHttpMessageConverter")
	public FastJsonHttpMessageConverter getJsonHttpMessageConverter() {
		FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
		FastJsonConfig config = new FastJsonConfig();
		config.setSerializerFeatures(SerializerFeature.WriteDateUseDateFormat, SerializerFeature.WriteMapNullValue);
		List<MediaType> mediaTypes = new ArrayList<>();
		mediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
		converter.setSupportedMediaTypes(mediaTypes);
		converter.setFastJsonConfig(config);
		return converter;
	}

	@Override
	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
		// 重新定义所有Converters
		converters.clear();
		converters.add(getStringHttpMessageConverter());
		converters.add(getJsonHttpMessageConverter());
	}

}