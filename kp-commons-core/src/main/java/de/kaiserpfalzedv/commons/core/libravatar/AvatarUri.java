/*
 * Libravatar -- Java Library for retrieving libravatars.
 *
 * The code in this package is taken from {@linkplain https://github.com/alessandroleite/libravatar-j}.
 *
 * It is licensed under a MIT license by Alessandro Leite.
 */
package de.kaiserpfalzedv.commons.core.libravatar;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;

@RequiredArgsConstructor
@Getter
public class AvatarUri implements FromBuilder {

	private final String email;
	private AvatarOptions options = new AvatarOptions().withoutHttps();

	/**
	 * 
	 * @param options
	 * @return
	 */
	public AvatarUri withOptions(AvatarOptions options){
		this.options = options;
		return this;
	}

	/**
	 * 
	 * @param options
	 * @return
	 */
	public byte[] download(AvatarOptions options) {
		this.options = options;
		return this.download();
	}

	/***
	 * 
	 * @return
	 */
	public byte[] download() {		
		InputStream is = null;
		try {
			URL url = new URL(this.buildUrl());
			return IOUtils.toByteArray(is = url.openStream());
		} catch (IOException exception) {
			throw new AvatarException(exception);
		} finally {
			IOUtils.closeQuietly(is);
		}
	}

	/**
	 * Builds the URI according to set options and the given email Address.
	 * @return The URI for retrieving the Libravatar.
	 */
	public String buildUrl() {
		StringBuilder sb = new StringBuilder(
				!this.options.isUseHttps() ? this.options.baseUri
						: this.options.secureBaseUri);
		
		sb.append(!this.options.isUseSHA256() ? DigestUtils.md5Hex(this.email.toLowerCase()
				.getBytes()) : DigestUtils.sha256Hex(this.email.toLowerCase().getBytes()));
		sb.append(this.options.getImageSize());

		return sb.toString();
	}
}