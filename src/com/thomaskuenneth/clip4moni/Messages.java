/*
 * Messages.java
 * 
 * This file is part of Clip4Moni.
 * 
 * Copyright (C) 2008 - 2015  Thomas Kuenneth
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

import java.util.ResourceBundle;

/**
 *
 * @author Thomas Kuenneth
 */
public class Messages {

    private static final ResourceBundle b = ResourceBundle
            .getBundle("com.thomaskuenneth.clip4moni.Messages");
    
    // menu items
    public static final String MI_QUIT = getString("MI_QUIT");
    public static final String MI_INFO = getString("MI_INFO");
    public static final String MI_EDITLIST = getString("MI_EDITLIST");
    public static final String MI_GETFROMCLIPBOARD = getString("MI_GETFROMCLIPBOARD");
    public static final String MI_CLIPBOARD = getString("MI_CLIPBOARD");
    public static final String MI_SETTINGS = getString("MI_SETTINGS");

    // buttons
    public static final String BTTN_UP = getString("BTTN_UP");
    public static final String BTTN_DOWN = getString("BTTN_DOWN");
    public static final String BTTN_DELETE = getString("BTTN_DELETE");
    public static final String BTTN_EDIT = getString("BTTN_EDIT");
    public static final String BTTN_SETTINGS_SNIPPETS_PATH = getString("BTTN_SETTINGS_SNIPPETS_PATH");
    public static final String BTTN_COPY = getString("BTTN_COPY");
    
    // misc.
    public static final String PROGNAME = getString("PROGNAME");
    public static final String LISTNAME = "Clip4Moni.list";
    public static final String GETFROMCLIPBOARD = getString("MI_GETFROMCLIPBOARD");

    public static String getString(String key) {
        return b.getString(key);
    }
}