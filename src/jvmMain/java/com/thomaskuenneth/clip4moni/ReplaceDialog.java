/*
 * ReplaceDialog.java
 *
 * This file is part of Clip4Moni.
 *
 * Copyright (C) 2019  Thomas Kuenneth
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
import java.awt.Dimension;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 * This dialog can replace text in the contents of the clipboard.
 *
 * @author thomas
 */
public class ReplaceDialog extends AbstractDialog {

    private final JTextField tfFrom, tfTo;

    private String contents;

    public ReplaceDialog(final String contents) {
        this.contents = contents;
        tfFrom = createTextField();
        tfTo = createTextField();
        init();
    }

    private void init() {
        Box b = Box.createVerticalBox();
//        final JTextArea textareaContents = new JTextArea(contents);
//        textareaContents.setEditable(false);
//        textareaContents.setWrapStyleWord(true);
//        textareaContents.setLineWrap(true);
//        b.add(new JScrollPane(textareaContents));
        b.add(createLabel(Messages.getString("STR_REPLACE_FROM")));
        b.add(tfFrom);
        b.add(createLabel(Messages.getString("STR_REPLACE_TO")));
        b.add(tfTo);
        b.add(new Box.Filler(new Dimension(0, 0),
                new Dimension(0, Short.MAX_VALUE),
                new Dimension(0, Short.MAX_VALUE)));
        setPreferredSize(UIHelper.PREFERRED_SIZE);
        add(b, BorderLayout.CENTER);
    }

    @Override
    public int showDialog() {
        int result = super.showDialog();
        if (result == JOptionPane.OK_OPTION) {
            contents = contents.replaceAll(tfFrom.getText(), tfTo.getText());
        }
        return result;
    }

    public String getContents() {
        return contents;
    }

    @Override
    public String getTitle() {
        return Messages.getString("TITLE_REPLACE");
    }

    @Override
    public int getOption() {
        return JOptionPane.OK_CANCEL_OPTION;
    }

    private JLabel createLabel(String s) {
        JLabel l = new JLabel(s);
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    private JTextField createTextField() {
        JTextField tf = new JTextField();
        tf.setAlignmentX(LEFT_ALIGNMENT);
        return tf;
    }
}
