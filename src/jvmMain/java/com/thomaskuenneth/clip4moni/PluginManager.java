/*
 * PluginManager.java
 *
 * This file is part of Clip4Moni.
 *
 * Copyright (C) 2008 - 2023  Thomas Kuenneth
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

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.rtf.RTFEditorKit;
import java.awt.Menu;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.UUID;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PluginManager {

    private static final String CLASSNAME = PluginManager.class.getName();
    private static final Logger LOGGER = Logger.getLogger(CLASSNAME);

    private static final String PAR = "##PA_R##";
    private static final String LINE = "##LINE_##";

    private static final String MI_STRIP_NUMBERS = Messages.getString("MI_STRIP_NUMBERS");
    private static final String MI_REMOVE_BLANKS = Messages.getString("MI_REMOVE_BLANKS");
    private static final String MI_REMOVE_SEMIVOWELS = Messages
            .getString("MI_REMOVE_SEMIVOWELS");
    private static final String MI_TAB_TO_SPACE = Messages.getString("MI_TAB_TO_SPACE");
    private static final String MI_UPPERCASE = Messages.getString("MI_UPPERCASE");
    private static final String MI_REMOVE_CRLF = Messages.getString("MI_REMOVE_CRLF");
    private static final String MI_REMOVE_SPECIALS = Messages.getString("MI_REMOVE_SPECIALS");
    private static final String MI_SHOW_CONTENTS = Messages.getString("MI_SHOW_CONTENTS");
    private static final String MI_HTML_TO_RTF = Messages.getString("MI_HTML_TO_RTF");
    private static final String MI_QUOTE = Messages.getString("MI_QUOTE");
    private static final String MI_REPLACE = Messages.getString("MI_REPLACE");
    private static final String MI_CREATE_UUID = Messages.getString("MI_CREATE_UUID");

    private static final String[] PLUGINS = new String[]{MI_SHOW_CONTENTS,
            MI_REPLACE, MI_REMOVE_BLANKS,
            MI_REMOVE_SEMIVOWELS, MI_TAB_TO_SPACE, MI_UPPERCASE,
            MI_REMOVE_CRLF, MI_REMOVE_SPECIALS, MI_STRIP_NUMBERS,
            MI_HTML_TO_RTF, MI_QUOTE, MI_CREATE_UUID};

    public static void populateMenu(Menu pm, ActionListener al) {
        for (String piName : PLUGINS) {
            UIHelper.createMenuItem(piName, pm, al, null);
            if (pm.getItemCount() == 2) {
                pm.addSeparator();
            }
        }
    }

    public static String callPlugin(String cmd, String in) {
        if (cmd.equalsIgnoreCase(MI_STRIP_NUMBERS)) {
            return processData(PluginManager::stripNumbers, in);
        } else if (cmd.equalsIgnoreCase(MI_REMOVE_BLANKS)) {
            return removeBlanks(in);
        } else if (cmd.equalsIgnoreCase(MI_REMOVE_SEMIVOWELS)) {
            return processData(PluginManager::removeSemivowels, in);
        } else if (cmd.equalsIgnoreCase(MI_TAB_TO_SPACE)) {
            return processData(PluginManager::convertTabToSpace, in);
        } else if (cmd.equalsIgnoreCase(MI_UPPERCASE)) {
            return toUpperCase(in);
        } else if (cmd.equalsIgnoreCase(MI_HTML_TO_RTF)) {
            return html2Rtf(in);
        } else if (cmd.equalsIgnoreCase(MI_REMOVE_CRLF)) {
            return processData(PluginManager::removeCrLf, in, false);
        } else if (cmd.equalsIgnoreCase(MI_REMOVE_SPECIALS)) {
            return processData(PluginManager::removeSpecials, in, false);
        } else if (cmd.equalsIgnoreCase(MI_SHOW_CONTENTS)) {
            new ShowContentsDialog(in).showDialog();
            return in;
        } else if (cmd.equalsIgnoreCase(MI_REPLACE)) {
            ReplaceDialog d = new ReplaceDialog(in);
            d.showDialog();
            return d.getContents();
        } else if (cmd.equalsIgnoreCase(MI_QUOTE)) {
            return quote(in);
        } else if (cmd.equalsIgnoreCase(MI_CREATE_UUID)) {
            return createRandomUUID();
        }
        return null;
    }

    private static String createRandomUUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    public static String quote(String in) {
        StringBuilder result = new StringBuilder();
        StringBuilder line = new StringBuilder();
        String[] words = in.split("\\s+");
        for (String w : words) {
            if ((line.length() + w.length()) >= 40) {
                result.append(line);
                result.append("\n");
                line.setLength(0);
            }
            if (line.length() == 0) {
                line.append("> ");
            } else {
                line.append(" ");
            }
            line.append(w);
        }
        if (line.length() > 0) {
            result.append(line);
        }
        return result.toString().trim();
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
            RTFEditorKit editorKitRtf = new RTFEditorKit();
            HTMLEditorKit editorKitHtml = new HTMLEditorKit();
            Document doc = editorKitHtml.createDefaultDocument();
            editorKitHtml.read(is, doc, 0);
            editorKitRtf.write(os, doc, 0, doc.getLength());
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

    public static String convertTabToSpace(String line) {
        return line.replaceAll("\t", "  ");
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
        int pos = 0;
        int len = line.length();
        char[] chars = new char[len];
        for (int i = 0; i < len; i++) {
            char ch = line.charAt(i);
            if (ch != ' ') {
                chars[pos++] = ch;
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

    public static String toUpperCase(String in) {
        return in.toUpperCase();
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

    private static String processData(
            Function<String, String> method,
            String in) {
        return processData(method, in, true);
    }

    private static String processData(
            Function<String, String> method,
            String in,
            boolean addEmptyLines) {
        StringBuilder sb = new StringBuilder();
        try (
                var sr = new StringReader(in);
                var br = new BufferedReader(sr)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                line = method.apply(line);
                if (line == null) continue;
                if (!addEmptyLines && (line.length() == 0)) {
                    continue;
                }
                if (sb.length() > 0) {
                    sb.append('\n');
                }
                sb.append(line);
            }
        } catch (IOException e) {
            LOGGER.throwing(CLASSNAME, "processData", e);
        }
        return sb.toString();
    }
}
