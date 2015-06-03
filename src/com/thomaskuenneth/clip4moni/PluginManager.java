/*
 * PluginManager.java
 * 
 * This file is part of Clip4Moni.
 * 
 * Copyright (C) 2008 - 2015  Thomas Kuenneth
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

import java.awt.Menu;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.rtf.RTFEditorKit;

public class PluginManager {

    private static final String CLASSNAME = PluginManager.class.getName();
    private static final Logger LOGGER = Logger.getLogger(CLASSNAME);

    private static final String PAR = "##PA_R##";
    private static final String LINE = "##LINE_##";

    private static final String pluginStripNumbers = Messages.getString("MI_STRIP_NUMBERS");
    private static final String pluginRemoveBlanks = Messages.getString("MI_REMOVE_BLANKS");
    private static final String pluginRemoveSemiVowels = Messages
            .getString("MI_REMOVE_SEMIVOWELS");
    private static final String pluginTab2Space = Messages.getString("MI_TAB_TO_SPACE");
    private static final String pluginUppercase = Messages.getString("MI_UPPERCASE");
    private static final String pluginRemoveCrLf = Messages.getString("MI_REMOVE_CRLF");
    private static final String pluginRemoveSpecials = Messages.getString("MI_REMOVE_SPECIALS");
    private static final String pluginShowContents = Messages.getString("MI_SHOW_CONTENTS");
    private static final String pluginHtml2Rtf = Messages.getString("MI_HTML_TO_RTF");

    private static final String[] plugins = new String[]{pluginShowContents, pluginRemoveBlanks,
        pluginRemoveSemiVowels, pluginTab2Space, pluginUppercase,
        pluginRemoveCrLf, pluginRemoveSpecials, pluginStripNumbers,
        pluginHtml2Rtf};

    public static String[] getPluginNames() {
        return plugins;
    }

    public static void populateMenu(Menu pm, ActionListener al) {
        String[] piNames = getPluginNames();
        for (String piName : piNames) {
            UIHelper.createMenuItem(piName, pm, al, null);
            if (pm.getItemCount()== 1) {
                pm.addSeparator();
            }
        }
    }

    /**
     * If plugins process each line individually, processData() is called.
     *
     * @param cmd
     * @param in
     * @return
     */
    public static String callPlugin(String cmd, String in) {
        if (cmd.equalsIgnoreCase(pluginStripNumbers)) {
            return processData("stripNumbers", in);
        } else if (cmd.equalsIgnoreCase(pluginRemoveBlanks)) {
            return processData("removeBlanks", in);
        } else if (cmd.equalsIgnoreCase(pluginRemoveSemiVowels)) {
            return processData("removeSemivowels", in);
        } else if (cmd.equalsIgnoreCase(pluginTab2Space)) {
            return processData("convertTabToSpace", in);
        } else if (cmd.equalsIgnoreCase(pluginUppercase)) {
            return processData("toUpperCase", in);
        } else if (cmd.equalsIgnoreCase(pluginHtml2Rtf)) {
            // need to treat the string as one unit
            return html2Rtf(in);
        } else if (cmd.equalsIgnoreCase(pluginRemoveCrLf)) {
            return processData("removeCrLf", in, false);
        } else if (cmd.equalsIgnoreCase(pluginRemoveSpecials)) {
            return processData("removeSpecials", in, false);
        } else if (cmd.equalsIgnoreCase(pluginShowContents)) {
            String contents = processData("doNothing", in, false);
            new ShowContentsDialog(contents).showDialog();
            return in;
        }
        return null;
    }

    public static String html2Rtf(String html) {
        InputStream is;
        OutputStream os;
        // need to replace a few things
        html = html.replaceAll("<br.*?>", LINE);
        html = html.replaceAll("</p>", PAR);
        html = html.replaceAll("<p.*?>", StringUtils.EMPTY);
        html = html.replaceAll("\n", StringUtils.EMPTY);
        html = html.replaceAll("<strong.*?>", "<b>");
        html = html.replaceAll("</strong>", "</b>");
        html = html.replaceAll("<em.*?>", "<i>");
        html = html.replaceAll("</em>", "</i>");
        try {
            is = new ByteArrayInputStream(html.getBytes());
            os = new ByteArrayOutputStream();
            RTFEditorKit editorkitRtf = new RTFEditorKit();
            HTMLEditorKit editorkitHtml = new HTMLEditorKit();
            Document doc = editorkitHtml.createDefaultDocument();
            editorkitHtml.read(is, doc, 0);
            editorkitRtf.write(os, doc, 0, doc.getLength());
            is.close();
            os.close();
            // need to replace a few things
            String result = os.toString();
            result = result.replaceAll(LINE, "\\\\line ");
            result = result.replaceAll(PAR, "\\\\par ");
            return result;
        } catch (IOException | BadLocationException e) {
            LOGGER.log(Level.SEVERE, "html2Rtf()", e);
        }
        return null;
    }

    public static String doNothing(String line) {
        return line;
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
            Class c = ClassLoader.getSystemClassLoader().loadClass(CLASSNAME);
            Class[] signature = new Class[]{String.class};
            Method m = c.getMethod(method, signature);
            return (String) m.invoke(null, line);
        } catch (ClassNotFoundException | NoSuchMethodException |
                SecurityException | IllegalAccessException |
                IllegalArgumentException | InvocationTargetException e) {
            LOGGER.throwing(CLASSNAME, method, e);
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
