/*
 * LAFComboboxModel.java
 *
 * This file is part of Clip4Moni.
 *
 * Copyright (C) 2013 - 2017  Thomas Kuenneth
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

import javax.swing.DefaultComboBoxModel;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;

/**
 * This combobox model contains the installed look and feels. When the model is
 * constructed, the current look and feel is set as the selected element.
 *
 * @author Thomas Kuenneth
 */
public class LAFComboboxModel extends DefaultComboBoxModel<UIManager.LookAndFeelInfo> {

    public LAFComboboxModel() {
        LookAndFeel current = UIManager.getLookAndFeel();
        UIManager.LookAndFeelInfo[] list = UIManager.getInstalledLookAndFeels();
        for (UIManager.LookAndFeelInfo info : list) {
            UIManager.LookAndFeelInfo xinfo = new UIManager.LookAndFeelInfo(info.getName(), info.getClassName()) {
                @Override
                public String toString() {
                    return getName();
                }
            };
            addElement(xinfo);
            if (xinfo.getName().equals(current.getName())) {
                setSelectedItem(xinfo);
            }
        }
    }
}