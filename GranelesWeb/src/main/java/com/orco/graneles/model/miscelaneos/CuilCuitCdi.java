/*
	CUIL, CUIT & CDI. Verification of keys and codes.

	Copyright (C) 2010  A. Fernandez

	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.orco.graneles.model.miscelaneos;

import static java.lang.Character.digit;
import static java.lang.Character.forDigit;
import static java.lang.Character.isDigit;
import static java.util.Arrays.copyOf;

import java.nio.CharBuffer;

/**
 * Immutable representation of a CUIL/CUIT/CDI (instances of this class are thread-safe.)
 * <p>
 * Structure (official):
 * 
 * <pre><tt> grammar CuilCuitCdi;
 * code     : head ('-')? body ('-')? (verifier)?;
 * head     : DIGIT DIGIT;
 * body     : DIGIT DIGIT DIGIT DIGIT DIGIT DIGIT DIGIT DIGIT;
 * verifier : DIGIT;
 * DIGIT    : '0'..'9';</tt></pre>
 * 
 * Possible inputs include: <code>hh-bbbbbbbb-v</code>, 
 * <code>hhbbbbbbbbv</code>, <code>hh-bbbbbbbb</code>, 
 * <code>hhbbbbbbbb</code>, <code>hh bbbbbbbb</code>, etc.
 * <br>
 * With <code>hh</code> header code, <code>bbbbbbbb</code> identifier body and
 * <code>v</code> verifier digit. Elements h, b, v are digits; v is optional 
 * (if not given, isVerified() will return <code>true</code>.)
 * <p>
 * Verification algorithm (unofficial):
 * 
 * <pre><tt> d : array of digit [10]
 * for (i:[0..3], j:[5..2]) sum := sum + d(i)*j 
 * for (i:[4..9], j:[7..2]) sum := sum + d(i)*j
 * v1 := sum mod 11
 * switch v1
 *   case 0  : v := 0 // exception 0
 *   case 1  : v := 9 // exception 1
 *   default : v := 11 - v1
 * return v</tt></pre>
 * 
 * Known header codes (unofficial):
 * 
 * <pre> 20: var�n
 * 27: mujer
 * 30: sujeto tributario
 * 23: persona f�sica (por excepci�n verificador)
 * 33: sujeto tributario (por excepci�n verificador)
 * 24: persona f�sica (por n�mero otorgado a otro sujeto)
 * 34: contribuyente (por n�mero otorgado a otro sujeto)</pre>
 * 
 * Possible references (in Spanish):
 * <p>
 * C�digo �nica de Identificaci�n Laboral (C.U.I.T.) / Clave �nica de
 * Identificaci�n Tributaria (C.U.I.T.) / Clave de Identificaci�n (C.D.I.),
 * Administraci�n Nacional de la Seguridad Social (ANSES), Sistema �nico de
 * Registro Laboral (S.U.R.L.) Ministerio de Trabajo, Empleo y Seguridad Social
 * de la Naci�n (MTSS), Rep�blica Argentina.
 * 
 * Leyes 24013, 24465, 24467 y 25013. Decretos Reglamentarios 
 * 2725/91, 738/95 y 1111/98. Resoluciones MTSS 11/92, 218/92, 
 * 499/95, 132/96. Disposici�n SURL 01/96. Circulares SURL 01/98, 
 * 03/98. Resoluci�n de la CESRLPE 01/95.
 * <p>
 * 
 * @author A. Fernandez
 * @version 0.1, 10/10/2010
 * 
 */
public class CuilCuitCdi {

	/**
	 * Builds a new code based on the input, calculates its verifier digit, and
	 * if available verifies.
	 * 
	 * @param input
	 *            containing at least 10 to 11 digits (extra digits, and
	 *            non-digits are ignored.)
	 * @throws IllegalArgumentException
	 *             if less than 10 digits are found.
	 */
	public CuilCuitCdi(CharSequence input) {
		for (int i = 0; i < input.length() && pos < code.length; i++) {
			final char ch = input.charAt(i);
			if (isDigit(ch)) {
				code[pos++] = digit(ch, I10);
			}
		}

		if (pos != I10 && pos != I11) {
			throw new IllegalArgumentException(
					ARGUMENT_REQUIRES_10_OR_11_DIGITS);
		}

		verifier = verifier();
		verified = verify();

	}

	/**
	 * @return calculated verifier digit from 0 to 9.
	 */
	private int verifier() {
		final int v2 = v2(code);
		if (exception = v2 <= 1) {
			return convertVerifier(v2);
		}
		return I11 - v2;
	}

	private static final int convertVerifier(final int v2) {
		return (v2 == 1) ? 9 : v2;
	}

	private boolean verify() {
		return (pos == I11) ? verifier == code[I10] : true;
	}

	/**
	 * (unofficial)
	 * @return true if the calculated verifier had an exception.
	 */
	public boolean isException() {
		return exception;
	}

	/**
	 * Self explanatory. (unofficial)
	 */
	public boolean isVerified() {
		return verified;
	}

	/**
	 * Self explanatory. (unofficial)
	 */
	public int getVerifier() {
		return verifier;
	}

	/**
	 * Possible genders based on head. 
	 */
	public static enum Gender {
		MALE, FEMALE, OTHER
	};

	/**
	 * (unofficial)
	 * 
	 * @return (head == 20)? MALE : (head == 27)? FEMALE : OTHER
	 */
	public Gender getGender() {
		final int head = headAsInt();
		return getGender(head);
	}

	private Gender getGender(final int head) {
		switch (head) {
		case 20:
			return Gender.MALE;
		case 27:
			return Gender.FEMALE;
		}
		return Gender.OTHER;
	}

	/**
	 * Try to guess gender. (untested)
	 * 
	 * @return (head == 23 || head == 24)? test(MALE) else test(FEMALE) else
	 *         OTHER
	 */
	public Gender guessGender() {
		final int head = headAsInt();
		final Gender gender = getGender(head);
		if (!Gender.OTHER.equals(gender)) {
			return gender;
		}
		if (head == 23 || head == 24) {
			final int[] copy = copyOf(code, code.length);
			if (verifier == verifierWithHeaderOverride(copy, 2, 0))
				return Gender.MALE;
			if (verifier == verifierWithHeaderOverride(copy, 2, 7))
				return Gender.FEMALE;
		}
		return Gender.OTHER;
	}

	/**
	 * Information based on head. (unofficial)
	 * 
	 * @return description in Spanish.
	 */
	public String headInfo() {
		final int head = headAsInt();
		switch (head) {
		case 20:
			return "varón";
		case 27:
			return "mujer";
		case 30:
			return "sujeto tributario";
		case 23:
			return "persona fisica (por excepción verificador)";
		case 33:
			return "sujeto tributario (por excepción verificador)";
		case 24:
			return "persona física (por número otorgado a otro sujeto)";
		case 34:
			return "contribuyente (por número otorgado a otro sujeto)";
		}
		return "otro";
	}

	/**
	 * Creates a string representing the code. (unofficial)
	 */
	public String toString() {
		final CharBuffer buf = CharBuffer.allocate(13);
		appendHead(buf);
		appendDash(buf);
		appendBody(buf);
		appendDash(buf);
		buf.append(forDigit(verifier, I10));
		return buf.flip().toString();
	}

	/**
	 * Creates a string representing the body.
	 */
	public String body() {
		final CharBuffer buf = CharBuffer.allocate(8);
		appendBody(buf);
		return buf.flip().toString();
	}

	/**
	 * Calculates head as int.
	 */
	public int headAsInt() {
		return code[0] * I10 + code[1];
	}

	/**
	 * Creates a string representing the head.
	 */
	public String head() {
		final CharBuffer buf = CharBuffer.allocate(2);
		appendHead(buf);
		return buf.flip().toString();
	}

	@SuppressWarnings("unused")
	private static final int v1UsingMX(final int[] code) {
		int v1 = 0;
		for (int i = 0; i < MX.length; i++) {
			v1 += code[i] * MX[i];
		}
		return v1;
	}

	private static final int v1(final int[] code) {
		int v1 = 0, i = 0, j;
		for (j = 5; i <= 3; i++, j--) {
			v1 += code[i] * j;
		}
		for (j = 7; i <= 9; i++, j--) {
			v1 += code[i] * j;
		}
		return v1;
	}

	private static final int v2(final int[] code) {
		return v1(code) % I11;
	}

	private static final int verifierWithHeaderOverride(int[] code, int d0,
			int d1) {
		code[0] = d0;
		code[1] = d1;
		return convertVerifier(v2(code));
	}

	private static final void appendDash(final CharBuffer buf) {
		buf.append(DASH);
	}

	private void appendHead(final CharBuffer buf) {
		append(buf, 0, I2);
	}

	private void appendBody(final CharBuffer buf) {
		append(buf, I2, I8);
	}

	private void append(final CharBuffer buf, int i, final int len) {
		for (int j = 0; j < len; j++) {
			buf.append(forDigit(code[i++], I10));
		}
	}

	private static final String ARGUMENT_REQUIRES_10_OR_11_DIGITS = "Argument requires 10 or 11 digits.";
	private static final char DASH = '-';
	private static final int I2 = 2; // head length or body begin
	private static final int I8 = 8; // body length
	private static final int I10 = 10; // code w/o verifier length or verifier
										// position
	private static final int I11 = 11; // code with verifier length
	private static final int[] MX = { 5, 4, 3, 2, 7, 6, 5, 4, 3, 2 }; // multiplier

	private final int[] code = new int[I11];
	private int pos; // code slot
	private final int verifier;
	private final boolean verified;
	private boolean exception;

}
