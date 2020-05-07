package org.linuxprobe.luava.springmvc.converter;

import org.springframework.core.convert.converter.Converter;

public class StringToBooleanConverter implements Converter<String, Boolean> {

    @Override
    public Boolean convert(String source) {
        if (source.toLowerCase().equals("yes") || source.toLowerCase().equals("true") || source.toLowerCase().equals("是") || source.equals("1")) {
            return true;
        } else if (source.toLowerCase().equals("no") || source.toLowerCase().equals("false") || source.toLowerCase().equals("否") || source.equals("0")) {
            return false;
        } else {
            throw new IllegalArgumentException(source + " cat not case to boolean");
        }
    }
}
