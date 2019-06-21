package org.linuxprobe.luava.springmvc.converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.converter.Converter;

/** String转Date */
public class StringToDateConverter implements Converter<String, Date> {
	public Map<String, SimpleDateFormat> dateFormatMap;

	public StringToDateConverter() {
		dateFormatMap = new HashMap<String, SimpleDateFormat>();
		dateFormatMap.put("^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}\\.[0-9]{3}Z$",
				new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
		dateFormatMap.put("^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}\\.[0-9]{3}\\sUTC$",
				new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
		dateFormatMap.put("^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}\\.[0-9]{3}$",
				new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS"));
		dateFormatMap.put("^[0-9]{4}-[0-9]{2}-[0-9]{2}\\s[0-9]{2}:[0-9]{2}:[0-9]{2}\\.[0-9]{3}$",
				new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"));
		dateFormatMap.put("^[0-9]{4}-[0-9]{2}-[0-9]{2}\\s[0-9]{2}:[0-9]{2}:[0-9]{2}$",
				new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
		dateFormatMap.put("^[0-9]{4}-[0-9]{2}-[0-9]{2}$", new SimpleDateFormat("yyyy-MM-dd"));
		dateFormatMap.put("^[0-9]{2}/[0-9]{2}/[0-9]{4}$", new SimpleDateFormat("MM/dd/yyyy"));
		dateFormatMap.put("^[0-9]{2}/[0-9]{2}/[0-9]{4}\\s[0-9]{2}:[0-9]{2}:[0-9]{2}$",
				new SimpleDateFormat("MM/dd/yyyy HH:mm:ss"));
	}

	@Override
	public Date convert(String source) {
		if (StringUtils.isBlank(source)) {
			return null;
		}
		if (source.matches("^[0-9]+$")) {
			return new Date(Long.valueOf(source));
		}
		Set<String> regexs = dateFormatMap.keySet();
		SimpleDateFormat simpleDateFormat = null;
		for (String regex : regexs) {
			if (source.matches(regex)) {
				if (source.matches("^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}\\.[0-9]{3}Z$")) {
					source = source.replace("Z", " UTC");
				}
				simpleDateFormat = dateFormatMap.get(regex);
				break;
			}
		}
		if (simpleDateFormat == null) {
			return null;
		}
		try {
			return simpleDateFormat.parse(source);
		} catch (ParseException e) {
			throw new IllegalArgumentException(e);
		}
	}
}
