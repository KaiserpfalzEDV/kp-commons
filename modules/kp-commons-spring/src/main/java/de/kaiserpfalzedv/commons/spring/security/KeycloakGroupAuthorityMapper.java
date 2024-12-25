package de.kaiserpfalzedv.commons.spring.security;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.extern.slf4j.XSlf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

/**
 * 
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de}
 * @since 3.3.1
 */
@Component
@RequiredArgsConstructor
@XSlf4j
public class KeycloakGroupAuthorityMapper implements GrantedAuthoritiesMapper {

    private final KeycloakGroupMapperProperties properties;

    @Override
    public Collection<? extends GrantedAuthority> mapAuthorities(Collection<? extends GrantedAuthority> authorities) {
        log.entry(authorities);

        Set<GrantedAuthority> result = new HashSet<>();

        authorities.forEach(authority -> extractRolesFromAuthority(result, authority));

        return log.exit(result);
    }

    private void extractRolesFromAuthority(Set<GrantedAuthority> result, GrantedAuthority authority) {
        log.entry(result, authority);

        if (authority instanceof OidcUserAuthority oidc) {
          if (log.isTraceEnabled()) {
                log.trace("Reading roles. oidc={}, attribute={}, groups={}", 
                    oidc, 
                    properties.getRoleAttribute(),
                    oidc.getUserInfo().getClaimAsString(properties.getRoleAttribute())
                );
            }

            result.addAll(extractGroupRoleFromOidcAuthority(oidc));
        }

        log.exit();
    }

    private Set<SimpleGrantedAuthority> extractGroupRoleFromOidcAuthority(OidcUserAuthority oidc) {
        log.entry(oidc);

        List<String> roles = oidc.getUserInfo().getClaimAsStringList(properties.getRoleAttribute());

        if (roles == null) {
            return log.exit(new HashSet<>());
        }

        return log.exit(
            roles.stream()
                .map(r -> "ROLE_" + r.toUpperCase())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet())
        );
    }
}