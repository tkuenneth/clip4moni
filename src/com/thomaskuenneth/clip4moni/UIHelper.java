/*
 * UIHelper.java
 * 
 * This file is part of Clip4Moni.
 * 
 * Copyright (C) 2008 - 2017  Thomas Kuenneth
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

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.event.ActionListener;
import java.awt.font.TextAttribute;
import java.net.URL;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.UIManager;

/**
 * This class offers ui-related helper methods.
 *
 * @author Thomas Kuenneth
 */
public class UIHelper {

    public static final Dimension PREFERRED_SIZE = new JTextArea(20, 55).getPreferredSize();

    private static final Logger LOGGER = Logger.getLogger(UIHelper.class.getName());

    private static final Font MENU_FONT;

    static {
        MENU_FONT = configureFont("MenuItem.font");
        configureFont("Button.font");
        configureFont("TextArea.font");
        configureFont("Label.font");
    }
    
    private static Font configureFont(String fontname) {
        Font f = UIManager.getFont(fontname);
        if (f != null) {
            LOGGER.log(Level.INFO,
                    MessageFormat.format("{0} ({1}) {2,number,integer} point",
                            f.getFamily(), f.getFontName(), f.getSize()));
            Map<TextAttribute, Object> attributes = new HashMap<>();
            attributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_REGULAR);
            attributes.put(TextAttribute.KERNING, TextAttribute.KERNING_ON);
            f = f.deriveFont(attributes);
        } else {
            LOGGER.log(Level.INFO, "MENU_FONT is null");
        }
        UIManager.getDefaults().put(fontname, f);
        return f;
    }

    /**
     * Creates a JButton, sets its text and ActionListener and sets AlignmentX
     * to CENTER_ALIGNMENT; if the parent object is not null, the new button is
     * added to this container
     *
     * @param parent the parent component
     * @param text the button text
     * @param l an action listener
     * @return a button
     */
    public static JButton createButton(Container parent, String text, ActionListener l) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.addActionListener(l);
        if (parent != null) {
            parent.add(button);
        }
        return button;
    }

    /**
     * Get an ImageIcon from a given resource.
     *
     * @param clazz a class
     * @param resource a resource
     * @return an ImageIcon
     */
    public static ImageIcon getImageIcon(Class clazz, String resource) {
        URL url = clazz.getClassLoader().getResource(resource);
        return new ImageIcon(url);
    }

    /**
     *
     * Creates a MenuItem, adds an ActionListener and adds the item to the
     * specified Menu (or subclasses like PopupMenu
     *
     * @param text text to display
     * @param menu the menu
     * @param al an action listener
     * @param cmd action command (if null is passed, setActionCommand() is not called)
     * @return a menu item
     */
    public static MenuItem createMenuItem(String text, Menu menu,
            ActionListener al, String cmd) {
        MenuItem item = new MenuItem(text);
        if (MENU_FONT != null) {
            item.setFont(MENU_FONT);
        }
        item.addActionListener(al);
        if (cmd != null) {
            item.setActionCommand(cmd);
        }
        menu.add(item);
        return item;
    }

    /**
     * Creates a menu
     *
     * @param title the title
     * @return the menu
     */
    public static Menu createMenu(String title) {
        Menu menu = new Menu(title);
        if (MENU_FONT != null) {
            menu.setFont(MENU_FONT);
        }
        return menu;
    }
}
