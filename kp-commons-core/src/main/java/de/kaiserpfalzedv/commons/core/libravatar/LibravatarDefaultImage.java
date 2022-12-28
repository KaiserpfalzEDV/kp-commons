/*
 * Libravatar -- Java Library for retrieving libravatars.
 *
 * The code in this package is taken from {@linkplain https://github.com/alessandroleite/libravatar-j}.
 *
 * It is licensed under a MIT license by Alessandro Leite.
 */
package de.kaiserpfalzedv.commons.core.libravatar;

public enum LibravatarDefaultImage {

	DEFAULT(""),
	
	MM("mm"),

	IDENTICON("identicon"),

	MONSTERID("monsterid"),

	WAVATAR("wavatar"),

	RETRO("retro"),

	NOT_FOUND("404");

	private final String code;

	private LibravatarDefaultImage(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}
}