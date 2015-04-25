/*
 * PluginManager.java
 * 
 * This file is part of Clip4Moni.
 * 
 * Copyright (C) 2008 - 2014  Thomas Kuenneth
 *
 * This program is free software; you can redistribute it and/or
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Method;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class PluginManager {

    private static final String pi1 = Messages.getString("MI_STRIP_NUMBERS");
    private static final String pi2 = Messages.getString("MI_REMOVE_BLANKS");
    private static final String pi3 = Messages
            .getString("MI_REMOVE_SEMIVOWELS");
    private static final String pi4 = Messages.getString("MI_TAB_TO_SPACE");
    private static final String pi5 = Messages.getString("MI_UPPERCASE");
    private static final String pi6 = Messages.getString("MI_REMOVE_CRLF");
    private static final String pi7 = Messages.getString("MI_REMOVE_SPECIALS");
    private static final String pi8 = Messages.getString("MI_SHOW_CONTENTS");

    private static final String TITLE_8 = Messages.getString("TITLE_SHOW_CONTENTS");

    public static String[] getPluginNames() {
        return new String[]{pi1, pi2, pi3, pi4, pi5, pi6, pi7, pi8};
    }

    public static String callPlugin(String cmd, String in) {
        if (cmd.equalsIgnoreCase(pi1)) {
            return processData("stripNumbers", in);
        } else if (cmd.equalsIgnoreCase(pi2)) {
            return processData("removeBlanks", in);
        } else if (cmd.equalsIgnoreCase(pi3)) {
            return processData("removeSemivowels", in);
        } else if (cmd.equalsIgnoreCase(pi4)) {
            return processData("convertTabToSpace", in);
        } else if (cmd.equalsIgnoreCase(pi5)) {
            return processData("toUpperCase", in);
        } else if (cmd.equalsIgnoreCase(pi6)) {
            return processData("removeCrLf", in, false);
        } else if (cmd.equalsIgnoreCase(pi7)) {
            return processData("removeSpecials", in, false);
        } else if (cmd.equalsIgnoreCase(pi8)) {
            String result = processData("doNothing", in, false);
            result = decode(result);
            JTextArea ta = new JTextArea(result);
            ta.setEditable(false);
            ta.setColumns(40);
            ta.setRows(10);
            ta.setWrapStyleWord(true);
            ta.setLineWrap(true);
            JOptionPane.showMessageDialog(null, new JScrollPane(ta), TITLE_8,
                    JOptionPane.PLAIN_MESSAGE);
            return in;
        }
        return null;
    }
    
    public static String doNothing(String line) {
        return line;
    }

    private static String decode(String s) {
        StringBuilder sb = new StringBuilder();
        int length = s.length();
        int i = 0;
        while (i < length) {
            char ch = s.charAt(i);
            i += 1;
            if (ch == '\\') {
                if ((i) == length) {
                    sb.append(ch);
                    continue;
                }
                ch = s.charAt(i);
                i += 1;
                switch (ch) {
                    case 'u':
                    case 'U':
                        int end = i + 4;
                        if (end > length) {
                            sb.append(ch);
                        } else {
                            String val = s.substring(i, end);
                            try {
                                ch = (char) Long.parseLong(val, 16);
                                sb.append(ch);
                                i = end;
                            } catch (NumberFormatException e) {
                                // TODO: Logging
                            }
                        }
                        break;
                    case '\\':
                        sb.append(ch);
                        break;
                }
            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }
    
    public static String convertTabToSpace(String line) {
        char[] src = {'\t'};
        String[] dst = {"  "};
        int len = line.length();
        int pos = 0;
        char[] result = new char[2 * len];
        for (int i = 0; i < len; i++) {
            char ch = line.charAt(i);
            boolean found = false;
            for (int j = 0; j < src.length; j++) {
                if (ch == src[j]) {
                    result[pos++] = dst[j].charAt(0);
                    result[pos++] = dst[j].charAt(1);
                    found = true;
                    break;
                }
            }
            if (found == false) {
                result[pos++] = ch;
            }
        }
        return new String(result, 0, pos);
    }

    public static String removeSemivowels(String line) {
        char[] src = {'\u00e4', '\u00c4', '\u00f6', '\u00d6', '\u00fc',
            '\u00dc', '\u00df'};
        String[] dst = {"ae", "Ae", "oe", "Oe", "ue", "Ue", "ss"};
        int len = line.length();
        int pos = 0;
        char[] result = new char[2 * len];
        for (int i = 0; i < len; i++) {
            char ch = line.charAt(i);
            boolean found = false;
            for (int j = 0; j < src.length; j++) {
                if (ch == src[j]) {
                    result[pos++] = dst[j].charAt(0);
                    result[pos++] = dst[j].charAt(1);
                    found = true;
                    break;
                }
            }
            if (!found) {
                result[pos++] = ch;
            }
        }
        return new String(result, 0, pos);
    }

    public static String removeBlanks(String line) {
        int len = line.length();
        char[] chars = new char[len];
        line.getChars(0, len, chars, 0);
        int pos = 0;
        for (int i = 0; i < len; i++) {
            chars[pos] = chars[i];
            if (chars[i] != ' ') {
                pos++;
            }
        }
        return new String(chars, 0, pos);
    }

    public static String removeCrLf(String line) {
        int len = line.length();
        char[] chars = new char[len];
        line.getChars(0, len, chars, 0);
        int pos = 0;
        for (int i = 0; i < len; i++) {
            char ch = chars[i];
            chars[pos] = ch;
            if ((ch != '\n') && (ch != '\r')) {
                pos++;
            }
        }
        return new String(chars, 0, pos);
    }

    public static String stripNumbers(String line) {
        for (int i = 0; i < line.length(); i++) {
            if (Character.isLetter(line.charAt(i))) {
                line = line.substring(i);
                break;
            }
        }
        return line;
    }

    public static String toUpperCase(String line) {
        return line.toUpperCase();
    }

    public static String removeSpecials(String line) {
        line = removeSemivowels(line);
        line = removeCrLf(line);
        int len = line.length();
        char[] chars = new char[len];
        line.getChars(0, len, chars, 0);
        int pos = 0;
        for (int i = 0; i < len; i++) {
            char ch = chars[i];
            if (Character.isDigit(ch) || (' ' == ch) || Character.isLetter(ch)
                    || ('.' == ch) || ('-' == ch)) {
                chars[pos++] = chars[i];
            }
        }
        return new String(chars, 0, pos);
    }

    private static String call(String method, String line) {
        try {
            Class c = ClassLoader.getSystemClassLoader().loadClass(
                    "com.thomaskuenneth.clip4moni.PluginManager");
            Class[] sig = new Class[1];
            sig[0] = String.class;
            Method m = c.getMethod(method, sig);
            return (String) m.invoke(null, line);
        } catch (Exception e) {
            System.err.println(e);
        }
        return null;
    }

    private static String processData(String method, String in) {
        return processData(method, in, true);
    }

    private static String processData(String method, String in,
            boolean addEmptyLines) {
        StringReader sr = null;
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        try {
            sr = new StringReader(in);
            br = new BufferedReader(sr);
            String line;
            while ((line = br.readLine()) != null) {
                line = call(method, line);
                if (!addEmptyLines && (line.length() == 0)) {
                    continue;
                }
                if (sb.length() > 0) {
                    sb.append('\n');
                }
                sb.append(line);
            }
        } catch (IOException e) {
        }
        if (br != null) {
            try {
                br.close();
            } catch (IOException e) {
                // intentionally no logging
            }
        }
        if (sr != null) {
            sr.close();
        }
        return sb.toString();
    }
}
