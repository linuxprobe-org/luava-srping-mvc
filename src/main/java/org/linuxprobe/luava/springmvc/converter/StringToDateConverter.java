package org.linuxprobe.luava.springmvc.converter;

import org.linuxprobe.luava.convert.impl.StringToDate;
import org.springframework.core.convert.converter.Converter;

import java.util.Date;

/**
 * String转Date
 */
public class StringToDateConverter implements Converter<String, Date> {
    private static final StringToDate stringToDate = new StringToDate();

    @Override
    public Date convert(String source) {
        return StringToDateConverter.stringToDate.convert(source);
    }
}
