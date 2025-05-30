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

package de.kaiserpfalzedv.commons.users.domain.model.apikey;


import de.kaiserpfalzedv.commons.api.BaseException;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.ToString;


/**
 * All exceptions around using api keys.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 04.05.2025
 */
@Getter
@ToString(callSuper = true)
public abstract class BaseApiKeyException extends BaseException {
  private final ApiKey apiKey;
  
  public BaseApiKeyException(@Nullable final ApiKey apiKey, @NotBlank final String message) {
    super(message);
    
    this.apiKey = apiKey;
  }
  
  public BaseApiKeyException(@Nullable final ApiKey apiKey, final String message, final Throwable cause) {
    super(message, cause);
    
    this.apiKey = apiKey;
  }
}
