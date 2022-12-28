/*
 * Libravatar -- Java Library for retrieving libravatars.
 *
 * The code in this package is taken from {@linkplain https://github.com/alessandroleite/libravatar-j}.
 *
 * It is licensed under a MIT license by Alessandro Leite.
 */
package de.kaiserpfalzedv.commons.core.libravatar;


import de.kaiserpfalzedv.commons.core.api.BaseSystemException;
import lombok.Getter;

/**
 * The class of all Libravatar related exceptions.
 *
 * @author alessandroleite {@literal @github}
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.3.0  2022-12-28
 */
@Getter
public class AvatarException extends BaseSystemException {
	/**
	 * Serial code version <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 2574665849051070802L;

	/**
	 * Constructs a new exception with the specified detail message.
	 *
	 * The cause is not initialized, and may subsequently be initialized by
	 * a call to {@link #initCause}.
	 *
	 * @param message the detail message. The detail message is saved for
	 *                later retrieval by the {@link #getMessage()} method.
	 * @since 2.3.0  2022-12-28
	 */
	public AvatarException(String message) {
		super(message);
	}

	/**
	 * Constructs a new exception with the specified cause and a detail
	 * message of {@code (cause==null ? null : cause.toString())} (which
	 * typically contains the class and detail message of {@code cause}).
	 * This constructor is useful for exceptions that are little more than
	 * wrappers for other throwables (for example, {@link java.security.PrivilegedActionException}).
	 *
	 * @param  cause the cause (which is saved for later retrieval by the
	 *         {@link #getCause()} method).  (A {@code null} value is
	 *         permitted, and indicates that the cause is nonexistent or
	 *         unknown.)
	 * @since 2.3.0  2022-12-28
	 */
	public AvatarException(final Throwable cause) {
		super(cause);
	}

	/**
	 * Constructs a new exception with the specified detail message and
	 * cause.
	 * <p>Note that the detail message associated with
	 * {@code cause} is <i>not</i> automatically incorporated in
	 * this exception's detail message.
	 *
	 * @param  message the detail message (which is saved for later retrieval
	 *         by the {@link #getMessage()} method).
	 * @param  cause the cause (which is saved for later retrieval by the
	 *         {@link #getCause()} method).  (A {@code null} value is
	 *         permitted, and indicates that the cause is nonexistent or
	 *         unknown.)
	 * @since 2.3.0  2022-12-28
	 */
	public AvatarException(final String message, final Throwable cause) {
		super(message, cause);
	}


	/**
	 * Constructs a new exception with the specified detail message, cause, suppression enabled or disabled, and
	 * writable stack trace enabled or disabled.
	 *
	 * @param  message the detail message.
	 * @param cause the cause.  (A {@code null} value is permitted,
	 * and indicates that the cause is nonexistent or unknown.)
	 * @param enableSuppression whether or not suppression is enabled
	 *                          or disabled
	 * @param writableStackTrace whether or not the stack trace should
	 *                           be writable
	 * @since 2.3.0  2022-12-28
	 */
	public AvatarException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}