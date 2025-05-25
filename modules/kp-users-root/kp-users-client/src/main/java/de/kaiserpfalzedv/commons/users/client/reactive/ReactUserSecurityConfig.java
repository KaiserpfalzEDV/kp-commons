/*
 * Copyright (c) 2025. Roland T. Lichti, Kaiserpfalz EDV-Service.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.kaiserpfalzedv.commons.users.client.reactive;


import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.aop.Advisor;
import org.springframework.aop.config.AopConfigUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Role;
import org.springframework.security.authorization.method.AuthorizationManagerAfterReactiveMethodInterceptor;
import org.springframework.security.authorization.method.AuthorizationManagerBeforeReactiveMethodInterceptor;
import org.springframework.security.authorization.method.PostFilterAuthorizationReactiveMethodInterceptor;
import org.springframework.security.authorization.method.PreFilterAuthorizationReactiveMethodInterceptor;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;

/**
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2025-05-25
 */
@Configuration
@EnableReactiveMethodSecurity
@Import({
    ReactUserDetailsService.class,
    ReactUserPreAuthorizationManager.class,
    ReactUserPostAuthorizationManager.class,
})
@XSlf4j
public class ReactUserSecurityConfig {
  @PostConstruct
  public void init() {
    log.entry();
    log.exit();
  }
  
  
  @Bean
  public BeanDefinitionRegistryPostProcessor aopConfig() {
    log.entry();
    
    return log.exit(AopConfigUtils::registerAspectJAutoProxyCreatorIfNecessary);
  }
  
  @Bean
  @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
  public PreFilterAuthorizationReactiveMethodInterceptor preFilterAuthorizationReactiveMethodInterceptor() {
    log.entry();
    
    return log.exit(new PreFilterAuthorizationReactiveMethodInterceptor());
  }
  
  @Bean
  @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
  Advisor preAuthorize() {
    log.entry();
    
    return log.exit(AuthorizationManagerBeforeReactiveMethodInterceptor.preAuthorize());
  }
  
  @Bean
  @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
  Advisor postAuthorize() {
    log.entry();
    
    return log.exit(AuthorizationManagerAfterReactiveMethodInterceptor.postAuthorize());
  }
  
  @Bean
  @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
  public PostFilterAuthorizationReactiveMethodInterceptor postFilterAuthorizationReactiveMethodInterceptor() {
    log.entry();
    
    return log.exit(new PostFilterAuthorizationReactiveMethodInterceptor());
  }
}
