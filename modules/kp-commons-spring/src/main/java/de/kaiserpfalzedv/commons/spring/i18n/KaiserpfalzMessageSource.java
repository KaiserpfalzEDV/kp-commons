package de.kaiserpfalzedv.commons.spring.i18n;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Component;
import de.kaiserpfalzedv.commons.core.i18n.ResourceBundleTranslator;

@Component
public class KaiserpfalzMessageSource extends ResourceBundleTranslator implements MessageSource {
    public static final long serialVersionUID = 0L;

    @Override
    public String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
        String result = getTranslation(code, locale, args);

        return !result.equals("!".concat(code)) ? result : defaultMessage;
    }

    // needs to be overridden because of the exception my original ResourceBundleTranslater throws that messes with the spring MessageSource
    @Override
    public String getMessage(String code, Object[] args, Locale locale) {
        return getTranslation(code, locale, args);
    }


    @Override
    public String getMessage(MessageSourceResolvable resolvable, Locale locale) {
        String[] codes = resolvable.getCodes();
        if (codes != null) {
            for (int i = 0; i < codes.length; i++) {
                String code = codes[i];

                String result = getTranslation(code, locale, resolvable.getArguments());

                if (! result.equals("!".concat(code))) {
                    return result;
                }
            }
        }

        return getTranslation(resolvable.getDefaultMessage(), locale, resolvable.getArguments());
    }
    
}
