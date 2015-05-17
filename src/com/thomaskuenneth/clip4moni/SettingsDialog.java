/*
 * SettingsDialog.java
 * 
 * This file is part of Clip4Moni.
 * 
 * Copyright (C) 2013 - 2015  Thomas Kuenneth
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
import javax.swing.JOptionPane;

/**
 * This class represents the settings dialog.
 *
 * @author Thomas Kuenneth
 */
public class SettingsDialog extends AbstractDialog {

    private final SettingsPanel p;

    public SettingsDialog() {
        p = new SettingsPanel();
        add(p, BorderLayout.CENTER);
    }

    @Override
    public String getTitle() {
        return Messages.TITLE_SETTINGS;
    }

    @Override
    public int getOption() {
        return JOptionPane.OK_CANCEL_OPTION;
    }

    public String getSnippetsDir() {
        return p.getSnippetsDir();
    }

    public String getLookAndFeel() {
        return p.getLookAndFeel();
    }
    
    public boolean isMacOSXWorkaroundActive() {
        return p.isMacOSXWorkaroundActive();
    }
}