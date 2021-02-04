package com.mrliuli.design.builder;

import javax.swing.*;
import java.awt.*;

/**
 * @author liu.li
 * @date 2021/2/4
 * @description 客厅
 */
public class Parlor {

    private String wall;
    private String tv;
    private String sofa;

    public String getWall() {
        return wall;
    }

    public void setWall(String wall) {
        this.wall = wall;
    }

    public String getTv() {
        return tv;
    }

    public void setTv(String tv) {
        this.tv = tv;
    }

    public String getSofa() {
        return sofa;
    }

    public void setSofa(String sofa) {
        this.sofa = sofa;
    }

    public void show() {
//        JFrame jf = new JFrame("建造者模式测试");
//        Container contentPane = jf.getContentPane();
//        JPanel p = new JPanel();
//        JScrollPane sp = new JScrollPane(p);
//        String parlour = wall + tv + sofa;
//        JLabel l = new JLabel(new ImageIcon("src/" + parlour + ".jpg"));
//        p.setLayout(new GridLayout(1, 1));
//        p.setBorder(BorderFactory.createTitledBorder("客厅"));
//        p.add(l);
//        contentPane.add(sp, BorderLayout.CENTER);
//        jf.pack();
//        jf.setVisible(true);
//        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        System.out.println(wall + sofa + tv);

    }

}
