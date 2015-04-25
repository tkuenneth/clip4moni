/*
 * AbstractDialog.java
 * 
 * This file is part of Clip4Moni.
 * 
 * Copyright (C) 2013  Thomas Kuenneth
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
import javax.swing.JPanel;

public abstract class AbstractDialog extends JPanel {

    public AbstractDialog() {
        setLayout(new BorderLayout());
    }

    public int showDialog() {
        int option = getOption();
        if (option == JOptionPane.OK_OPTION) {
            JOptionPane.showMessageDialog(null, this, getTitle(),
                    JOptionPane.PLAIN_MESSAGE);
            return option;
        } else {
            return JOptionPane.showConfirmDialog(null, this, getTitle(),
                    getOption(), JOptionPane.PLAIN_MESSAGE);
        }
    }

    public abstract String getTitle();

    public abstract int getOption();
}
