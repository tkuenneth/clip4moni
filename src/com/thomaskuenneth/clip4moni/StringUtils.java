/*
 * StringUtils.java
 * 
 * This file is part of Clip4Moni.
 * 
 * Copyright (C) 2015  Thomas Kuenneth
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

/**
 * This class contains static string utility methods.
 *
 * @author thomas
 */
public final class StringUtils {
    
    public static final String UNKNOWN = "???";
    
    private StringUtils() {
    }

    /**
     * Ensures that a string is not null.
     *
     * @param s string to be checked
     * @return the string or "???"
     */
    public static String ensureNotNull(String s) {
        return (s != null) ? s : UNKNOWN;
    }
}
