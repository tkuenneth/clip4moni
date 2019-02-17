/*
 * AboutView.java
 * 
 * This file is part of Clip4Moni.
 * 
 * Copyright (C) 2015 - 2017  Thomas Kuenneth
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
import java.awt.Dimension;
import java.awt.Font;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIDefaults;
import javax.swing.event.HyperlinkEvent;

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
        JEditorPane pane = new JEditorPane();
        pane.setPreferredSize(UIHelper.PREFERRED_SIZE);
        Font font = pane.getFont();
        String html = FileUtilities.getResourceAsString(getClass(), URL_INFO);
        String version = getClass().getPackage().getImplementationVersion();
        if (version == null) {
            version = StringUtils.UNKNOWN;
        }
        Dimension screenSize = Helper.getScreenSize();
        html = MessageFormat.format(html, font.getFamily(), version,
                System.getProperty("java.version"), System.getProperty("java.vendor"),
                System.getProperty("os.name"), System.getProperty("os.version"),
                Integer.toString(screenSize.width),
                Integer.toString(screenSize.height),
                Integer.toString(Helper.SCREEN_RESOLUTION));
        pane.setContentType("text/html");
        pane.setText(html);
        pane.addHyperlinkListener((HyperlinkEvent e) -> {
            if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
                try {
                    Desktop.getDesktop().browse(e.getURL().toURI());
                } catch (IOException | URISyntaxException ex) {
                    LOGGER.throwing(CLASS_NAME, "hyperlinkUpdate", ex);
                }
            }
        });
        pane.setCaretPosition(0);
        pane.setEditable(false);
        pane.setBackground(getBackground());
        // Workaround for Nimbus
        UIDefaults defaults = new UIDefaults();
        defaults.put("EditorPane[Enabled].backgroundPainter", getBackground());
        pane.putClientProperty("Nimbus.Overrides", defaults);
        pane.putClientProperty("Nimbus.Overrides.InheritDefaults", true);
        pane.setBackground(getBackground());
        // end workaround
        JScrollPane scrollpane = new JScrollPane(pane);
        scrollpane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollpane, BorderLayout.CENTER);
    }
}
