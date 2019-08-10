package org.zongf.plugins.idea.util;

import org.junit.Test;

import javax.swing.*;
import java.awt.*;

/**
 * @Description:
 * @author: zongf
 * @date: 2019-08-10 16:15
 */
public class LayoutTest {

    public static void main(String[] args) {

        Frame frame = new Frame("计算器");
        Panel panel = new Panel();
        panel.add(new TextField(30));


        Panel panel2 = new Panel();
        panel2.setLayout(new GridLayout(3, 5, 4, 4));

        String[] names = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "+", "-", "*", "/", "."};

        for (String name : names) {
            panel2.add(new Button(name));
        }

        frame.add(panel2);
        frame.pack();
        frame.setVisible(true);

    }


}
