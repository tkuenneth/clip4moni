/*
 * ShowContentsDialog.java
 * 
 * This file is part of Clip4Moni.
 * 
 * Copyright (C) 2015 - 2018  Thomas Kuenneth
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

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * This dialog shows the contents of the clipboard.
 *
 * @author thomas
 */
public class ShowContentsDialog extends AbstractDialog {

    private final String contents;

    public ShowContentsDialog(final String contents) {
        this.contents = contents;
        init();
    }

    private void init() {
        // text area
        final JTextArea textareaContents = new JTextArea(contents);
        textareaContents.setEditable(false);
        textareaContents.setWrapStyleWord(true);
        textareaContents.setLineWrap(true);
        setPreferredSize(UIHelper.PREFERRED_SIZE);
        add(new JScrollPane(textareaContents), BorderLayout.CENTER);
        // checkbox
        final JCheckBox checkboxDecode = new JCheckBox(Messages.getString("CHECKBOX_DECODE"));
        checkboxDecode.addItemListener((ItemEvent e) -> {
            String s = checkboxDecode.isSelected() ? StringUtils.decode(contents) : contents;
            textareaContents.setText(s);
        });
        add(checkboxDecode, BorderLayout.NORTH);
        checkboxDecode.setSelected(false);
    }

    @Override
    public String getTitle() {
        return Messages.getString("TITLE_SHOW_CONTENTS");
    }

    @Override
    public int getOption() {
        return JOptionPane.PLAIN_MESSAGE;
    }
}
