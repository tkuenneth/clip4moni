/*
 * Entry.java
 * 
 * This file is part of Clip4Moni.
 * 
 * Copyright (C) 2008 - 2013  Thomas Kuenneth
 *
 * Clip4Moni is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package com.thomaskuenneth.clip4moni;

public class Entry {

	private String key, value;

	public Entry(String all) {
		setAll(all);
	}

	public Entry(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public void setAll(String all) {
		int pos = all.indexOf('|');

                if (pos > 0) {
			int len = Integer.parseInt(all.substring(0, pos));
			pos += 1;
			setKey(all.substring(pos, pos + len));
			value = all.substring(pos + len + 1);
		}
	}

	public String getAll() {
		return Integer.toString(key.length()) + '|' + key + '|' + value;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

        @Override
	public String toString() {
		return getKey();
	}
}
