/*
 * MacHelp.java
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

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * This is a Mac-specific helper class. It executes AppleScript scripts.
 *
 * @author Thomas Kuenneth
 */
public class MacHelp {

    private static final String TAG = MacHelp.class.getName();
    private static final Logger LOGGER = Logger.getLogger(TAG);
    private static final ScriptEngine engine = getScriptEngine();

    /**
     * If running on Mac OS X, this method tries to obtain an AppleScript script
     * engine.
     *
     * @return an instance of an AppleScript script engine or {@code null}
     */
    private static ScriptEngine getScriptEngine() {
        if (Helper.isMacOSX()) {
            ScriptEngineManager sem = new ScriptEngineManager();
            List<ScriptEngineFactory> list = sem.getEngineFactories();
            for (ScriptEngineFactory factory : list) {
                String engineName = factory.getEngineName();
                String engineVersion = factory.getEngineVersion();
                String langName = factory.getLanguageName();
                String langVersion = factory.getLanguageVersion();
                LOGGER.log(Level.INFO, "{0} {1} {2} {3}", new Object[]{engineName, engineVersion, langName, langVersion});
                List<String> mimeTypes = factory.getMimeTypes();
                for (String mimeType : mimeTypes) {
                    if (mimeType.contains("applescript")) {
                        return factory.getScriptEngine();
                    }
                }
            }
        }
        return null;
    }

    /**
     * Activates an app.
     *
     * @param name app to activate
     */
    public static void activateApp(String name) {
        if (name != null) {
            String program = "tell application \"" + name + "\"\nactivate\nend tell";
            run(program, "activateApp");
        }
    }

    /**
     * Gets the name of the frontmost app.
     *
     * @return name of the frontmost app or {@code null}
     */
    public static String getFrontmostApp() {
        String script = "tell application \"System Events\"\nitem 1 of (get name of processes whose frontmost is true)\nend tell";
        Object result = run(script, "getFrontmostApp");
        if (result != null) {
            return result.toString();
        }
        return null;
    }

    /**
     * Executes a script.
     *
     * @param script the script
     * @param methodName used if an exception is thrown
     * @return the result of the script execution or {@code null}
     */
    private static Object run(String script, String methodName) {
        if ((Helper.isMacOSXWorkaroundActive()) && (engine != null)) {
            try {
                return engine.eval(script);
            } catch (ScriptException ex) {
                LOGGER.throwing(TAG, methodName, ex);
            }
        }
        return null;
    }
}
