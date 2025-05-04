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

package de.kaiserpfalzedv.commons.users.domain.model.abac;


import com.googlecode.aviator.runtime.type.AviatorBoolean;
import com.googlecode.aviator.runtime.type.AviatorObject;
import de.kaiserpfalzedv.commons.users.domain.model.user.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.XSlf4j;
import org.casbin.jcasbin.util.function.CustomFunction;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * This is a casbin custom function to check for ownership.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 01.05.2025
 */
@XSlf4j
public class OwnedBy extends CustomFunction {
  public static final String OWNED = "owned";
  public static final String SELF = "self";
  
  @Override
  public String getName() {
    return "ownedBy";
  }
  
  @Override
  public AviatorObject call(Map<String, Object> env, AviatorObject policy, AviatorObject target, AviatorObject subject) {
    String pol = (String) policy.getValue(env);
    @SuppressWarnings("unchecked")
    HasOwner<UUID> tar = (HasOwner<UUID>) target.getValue(env);
    User user = (User) subject.getValue(env);
    
    log.entry(user, tar, pol, env);
    
    log.debug("Checking access. policy={}, target={}/{}/{}, subject={}/{}/{}", pol,
        tar.getABACObjectName(), tar.getId(), tar.getOwnerId(),
        user.getId(), user.getNameSpace(), user.getName());
    
    if (
          checkRole(user, env)
          && checkTarget(tar, pol, env)
          && checkAction(tar, pol, env)
          && checkOwnership(user, tar, pol)
    ) {
      log.info("Policy check passed. policy={}, target={}/{}/{}, subject={}/{}/{}", pol,
          tar.getABACObjectName(), tar.getId(), tar.getOwnerId(),
          user.getId(), user.getNameSpace(), user.getName());
      return log.exit(AviatorBoolean.TRUE);
    }

    return log.exit(AviatorBoolean.FALSE);
  }
  
  private boolean checkRole(@NotNull final User caller, @NotNull @NotEmpty final Map<String, Object> env) {
    log.entry(caller);
    
    String role = "ROLE_" + env.get("p_sub");
    
    if (! caller.hasRole(role)) {
      log.debug("Policy does not apply. User doesn't have role. role='{}', user='{}/{}'", role, caller.getNameSpace(), caller.getName());
      return false;
    }
    
    log.trace("Policy may apply. User has role. role='{}', user='{}/{}'", role, caller.getNameSpace(), caller.getName());
    return log.exit(true);
  }
  
  
  private boolean checkTarget(@NotNull final Object target, @NotNull final String policy, @NotNull @NotEmpty final Map<String, Object> env) {
    log.entry(target, policy, env);
    
    String[] policyParts = policy.split("/");
    String policyName = policyParts[0];

    String targetName = getTargetTypeName(target);
    
    if (!targetName.equals(policyName) && ! "*".equals(policyName)) {
      log.debug("Policy does not match. target='{}', policyName='{}'", targetName, policyName);
      return log.exit(false);
    }
    
    return log.exit(true);
  }
  
  private String getTargetTypeName(Object target) {
    if (target instanceof String) {
      return (String) target;
    }
    
    if (target instanceof HasOwner) {
      return ((HasOwner<?>)target).getABACObjectName();
    }
    
    return target.getClass().getSimpleName();
  }
  
  
  private boolean checkOwnership(@NotNull final User user, @NotNull final HasOwner<UUID> target, @NotBlank final String policy) {
    log.entry(user, target.getABACObjectName(), target.getId(), policy);
    
    String[] policyParts = policy.split("/");
    String policyName = policyParts[0];
    String policyId = (policyParts.length > 1) ? policyParts[1] : "*";
    
    if (
        "*".equals(policyId)
        || checkOwnership(user, policyId, OWNED, target.getOwnerId(), user.getId())
        || checkOwnership(user, policyId, SELF, target.getOwnerId(), user.getId())
    ) {
      log.trace("Policy does match. policy='{}', policyId='{}' owner='{}'", policyName, policyId, target.getOwnerId());
      return log.exit(true);
    }
    
    log.debug("Wrong owner. policy='{}', expected='{}'", policy, target.getOwnerId());
    return log.exit(false);
  }
  
  private boolean checkOwnership(@NotNull final User user, @NotBlank final String policy, @NotEmpty final String ownerType, @NotNull final UUID expected, @NotNull final UUID actual) {
    log.entry(user, ownerType, policy, expected, actual);
    
    
    if (ownerType.equals("*")
        || (ownerType.equals(policy) && expected.equals(actual))
    ) {
      return log.exit(true);
    }
    
    return log.exit(false);
  }
  
  
  private boolean checkAction(@NotNull final HasOwner<UUID> target, @NotNull final String policy,
                           @NotNull final Map<String, Object> env) {
    log.entry(target, policy);
    
    Set<String> allowed = getAllowedActions(env);
    String action = (String) env.get("r_act");
    
    if (
        !(
          allowed.contains("*")
          || allowed.contains(action)
        )
    ) {
      log.debug("Policy does not match. action='{}', allowed={}", action, allowed);
      return log.exit(false);
    }
    
    log.trace("Policy does match. action='{}', policy='{}'", action, allowed);
    return log.exit(true);
  }
  
  private Set<String> getAllowedActions(@NotNull final Map<String, Object> env) {
    log.entry(env);
    
    return log.exit(Arrays.stream(((String) env.get("p_act")).split(" ")).collect(Collectors.toSet()));
  }
}
