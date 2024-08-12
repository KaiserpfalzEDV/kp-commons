package de.kaiserpfalzedv.commons.spring.security;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de}
 * @since 3.3.1
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class KeycloakGroupAuthorityMapper implements GrantedAuthoritiesMapper {

    private final KeycloakGroupMapperProperties properties;

    @Override
    public Collection<? extends GrantedAuthority> mapAuthorities(Collection<? extends GrantedAuthority> authorities) {
        Set<GrantedAuthority> result = new HashSet<>();

        authorities.forEach(authority -> extractRolesFromAuthority(result, authority));

        log.debug("Roles mapped. roles={}", result);
        return result;
    }

    private void extractRolesFromAuthority(Set<GrantedAuthority> result, GrantedAuthority authority){
        if (authority instanceof OidcUserAuthority) {
            OidcUserAuthority oidc = (OidcUserAuthority) authority;

            if (log.isTraceEnabled()) {
                log.trace("Reading roles. oidc={}, attribute={}, groups={}", 
                    oidc, 
                    properties.getRoleAttribute(),
                    oidc.getUserInfo().getClaimAsString(properties.getRoleAttribute())
                );
            }

            result.addAll(extractGroupRoleFromOidcAuthority(oidc));
        }
    }

    private Set<SimpleGrantedAuthority> extractGroupRoleFromOidcAuthority(OidcUserAuthority oidc) {
        return oidc.getUserInfo().getClaimAsStringList(properties.getRoleAttribute())
            .stream()
            .map(r -> "ROLE_" + r.toUpperCase())
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toSet());
    }
}