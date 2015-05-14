/*
 * AboutView.java
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

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Font;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

/**
 * This class displays program and copyright information
 *
 * @author Thomas Kuenneth
 */
public class AboutView extends JPanel {

    private static final String CLASS_NAME = AboutView.class.getName();
    private static final Logger LOGGER = Logger.getLogger(CLASS_NAME);

    private static final String URL_INFO = "/com/thomaskuenneth/clip4moni/assets/info.txt";

    public AboutView() {
        super(new BorderLayout());
        createUI();
    }

    private void createUI() {
        JTextArea a = new JTextArea(20, 50);
        JEditorPane pane = new JEditorPane();
        pane.setPreferredSize(a.getPreferredSize());
        Font font = pane.getFont();
        String html = FileUtilities.getResourceAsString(getClass().getResource(URL_INFO));
        String version = getClass().getPackage().getImplementationVersion();
        try {
            String date = StringUtils.UNKNOWN;
            if (version != null) {
                Date d = new SimpleDateFormat("yyMMdd").parse(version);
                date = DateFormat.getDateInstance().format(d);
            }
            html = MessageFormat.format(html, font.getFamily(), date,
                    System.getProperty("java.version"), System.getProperty("java.vendor"),
                    System.getProperty("os.name"), System.getProperty("os.version"));
        } catch (ParseException ex) {
            LOGGER.log(Level.SEVERE, "info()", ex);
        }
        pane.setContentType("text/html");
        pane.setText(html);
        pane.addHyperlinkListener(new HyperlinkListener() {

            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
                    try {
                        Desktop.getDesktop().browse(e.getURL().toURI());
                    } catch (IOException ex) {
                        LOGGER.throwing(CLASS_NAME, "hyperlinkUpdate", ex);
                    } catch (URISyntaxException ex) {
                        LOGGER.throwing(CLASS_NAME, "hyperlinkUpdate", ex);
                    }
                }
            }
        });
        pane.setCaretPosition(0);
        pane.setEditable(false);
        pane.setBackground(getBackground());
        JScrollPane scrollpane = new JScrollPane(pane);
        scrollpane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollpane, BorderLayout.CENTER);
    }
}
