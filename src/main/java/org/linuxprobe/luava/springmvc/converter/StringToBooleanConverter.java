package org.linuxprobe.luava.springmvc.converter;

import org.linuxprobe.luava.convert.impl.StringToBoolean;
import org.springframework.core.convert.converter.Converter;

public class StringToBooleanConverter implements Converter<String, Boolean> {
    private static final StringToBoolean stringToBoolean = new StringToBoolean();

    @Override
    public Boolean convert(String source) {
        return StringToBooleanConverter.stringToBoolean.convert(source);
    }
}
