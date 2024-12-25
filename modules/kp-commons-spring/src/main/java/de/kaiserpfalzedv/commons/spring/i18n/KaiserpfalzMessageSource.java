package de.kaiserpfalzedv.commons.spring.i18n;

import java.io.Serial;
import java.util.Locale;

import lombok.NonNull;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Component;
import de.kaiserpfalzedv.commons.core.i18n.ResourceBundleTranslator;

@Component
@XSlf4j
public class KaiserpfalzMessageSource extends ResourceBundleTranslator implements MessageSource {
    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public String getMessage(@NonNull String code, Object[] args, String defaultMessage, @NonNull Locale locale) {
        log.entry(code, args, defaultMessage, locale);

        String result = getTranslation(code, locale, args);

        return log.exit(!result.equals("!".concat(code)) ? result : defaultMessage);
    }

    // needs to be overridden because of the exception my original ResourceBundleTranslater throws that messes with the spring MessageSource
    @Override
    @NonNull
    public String getMessage(@NonNull String code, Object[] args, @NonNull Locale locale) {
        log.entry(code, args, locale);

        return log.exit(getTranslation(code, locale, args));
    }


    @Override
    @NonNull
    public String getMessage(@NonNull MessageSourceResolvable resolvable, @NonNull Locale locale) {
        log.entry(resolvable, locale);

        String[] codes = resolvable.getCodes();
        if (codes != null) {
          for (String code : codes) {
            String result = getTranslation(code, locale, resolvable.getArguments());

            if (!result.equals("!".concat(code))) {
              return log.exit(result);
            }
          }
        }

        return log.exit(getTranslation(resolvable.getDefaultMessage(), locale, resolvable.getArguments()));
    }
}
