package com.mapsengineering.workeffortext.widgets;

import java.awt.Color;

/**
 * Supporto esteso per la classe Color che non gupporta il get con il nome del colore ma solo con il codice esadecimale
 */
public enum Colors {
    WHITE(Color.WHITE), GREEN(Color.GREEN), RED(Color.RED), GRAY(Color.GRAY), YELLOW(Color.YELLOW), BLACK(Color.BLACK), BLUE(Color.BLUE), LIGHT_GRAY(Color.LIGHT_GRAY);

    private Color color;

    private Colors(Color color) {
        this.color = color;
    }

    private Color getColor() {
        return this.color;
    }

    public static Color decodeColor(String name) {
        try {
            return Colors.valueOf(name.toUpperCase()).getColor();
        } catch (Exception e) {
            //Default
            return Colors.WHITE.getColor();
        }
    }
}
