/*******************************************************************************
 * Copyright (c) 2014 Eugen Covaci.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Eugen Covaci - initial API and implementation
 ******************************************************************************/
package org.vat.validator.impl;


/**
 * Spanish VAT number validator.
 * 
 * @author eugen covaci
 * 
 */
public class ES extends AbstractVatFormalValidator {

	@Override
	public boolean validateDigits(String vatNumber) {

		// Checks the check digits of a Spanish VAT number.

		int total = 0;
		int temp = 0;
		int[] multipliers = { 2, 1, 2, 1, 2, 1, 2 };
		String[] esexp = new String[4];
		esexp[0] = "^[A-H|J|U|V]\\d{8}$";
		esexp[1] = "^[A-H|N-S|W]\\d{7}[A-J]$";
		esexp[2] = "^[0-9|Y|Z]\\d{7}[A-Z]$";
		esexp[3] = "^[K|L|M|X]\\d{7}[A-Z]$";
		int i = 0;

		// National juridical entities
		if (vatNumber.matches(esexp[0])) {

			// Extract the next digit and multiply by the counter.
			for (i = 0; i < 7; i++) {
				temp = Integer.parseInt(vatNumber.substring(i + 1, i + 2)) * multipliers[i];
				if (temp > 9)
					total += Math.floor(temp / 10) + temp % 10;
				else
					total += temp;
			}
			// Now calculate the check digit itself.
			total = 10 - total % 10;
			if (total == 10) {
				total = 0;
			}

			// Compare it with the last character of the VAT number. If it's the
			// same, then it's valid.
			return total == Integer.parseInt(vatNumber.substring(8, 9));
		}

		// Juridical entities other than national ones
		else if (vatNumber.matches(esexp[1])) {

			// Extract the next digit and multiply by the counter.
			for (i = 0; i < 7; i++) {
				temp = Integer.parseInt(vatNumber.substring(i + 1, i + 2)) * multipliers[i];
				if (temp > 9)
					total += Math.floor(temp / 10) + temp % 10;
				else
					total += temp;
			}

			// Now calculate the check digit itself.
			total = 10 - total % 10;

			// Compare it with the last character of the VAT number. If it's the
			// same, then it's valid.
			return (char) (total + 64) == vatNumber.charAt(8);
		}

		// Personal number (NIF) (starting with numeric of Y or Z)
		else if (vatNumber.matches(esexp[2])) {
			String tempnumber = new String(vatNumber);
			if (tempnumber.substring(0, 1) == "Y")
				tempnumber = tempnumber.replace("Y", "1");
			if (tempnumber.substring(0, 1) == "Z")
				tempnumber = tempnumber.replace("Z", "2");
			return tempnumber.charAt(8) == "TRWAGMYFPDXBNJZSQVHLCKE"
					.charAt(Integer.parseInt(tempnumber.substring(0, 8)) % 23);
		}

		// Personal number (NIF) (starting with K, L, M, or X)
		else if (vatNumber.matches(esexp[3])) {
			return vatNumber.charAt(8) == "TRWAGMYFPDXBNJZSQVHLCKE"
					.charAt(Integer.parseInt(vatNumber.substring(1, 8)) % 23);
		}

		else {
			return false;
		}
	}

	@Override
	public String[] getRegexArray() {
		return new String[] { "^([A-Z]\\d{8})$", "^([A-H|N-S|W]\\d{7}[A-J])$", "^([0-9|Y|Z]\\d{7}[A-Z])$",
				"^([K|L|M|X]\\d{7}[A-Z])$" };
	}

}
