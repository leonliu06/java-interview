package com.mrliuli;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author liu.li
 * @date 2021/4/19
 * @description
 */
public class KeyRouteTest {


    public static void main(String[] args) {

        List<LinkedList<Step>> processes = initProcess();


    }


    private static LinkedList<Step> getKeyProcess(List<LinkedList<Step>> processes) {

        if (processes == null || processes.size() < 1) {
            return null;
        }

        BigDecimal totalHours = BigDecimal.ZERO;
        LinkedList<Step> keyProcess = processes.get(0);

        for (LinkedList<Step> item : processes) {
            if (item == null || item.size() < 1) {
                continue;
            }
            BigDecimal temp = item.stream().map(Step::getHours).reduce(BigDecimal.ZERO, BigDecimal::add);
            if (temp.compareTo(totalHours) > 0) {
                totalHours = temp;
                keyProcess = item;
            }
        }

        return keyProcess;

    }


    /**
     * 初始化工艺流程
     *
     * @return
     */
    private static List<LinkedList<Step>> initProcess() {

        Step desktopCutting = new Step("桌面开料", BigDecimal.valueOf(0.5));
        Step polishing = new Step("抛光", BigDecimal.valueOf(0.5));
        Step assembling = new Step("组装", BigDecimal.valueOf(0.5));
        Step coating = new Step("油漆", BigDecimal.valueOf(0.5));
        Step assemblingAll = new Step("全桌组装", BigDecimal.ONE);

        Step legCutting = new Step("桌腿开料", BigDecimal.valueOf(0.5));
        Step grinding = new Step("打磨", BigDecimal.ONE);

        Step drawerCutting = new Step("抽屉板开料", BigDecimal.ONE);
        Step drawerAssembling = new Step("抽屉组装", BigDecimal.ONE);
        Step drawerPainting = new Step("抽屉油漆", BigDecimal.valueOf(2));


        LinkedList<Step> process1 = new LinkedList<>();

        process1.add(desktopCutting);
        process1.add(polishing);
        process1.add(assembling);
        process1.add(coating);
        process1.add(assemblingAll);


        LinkedList<Step> process2 = new LinkedList<>();
        process2.add(legCutting);
        process2.add(grinding);
        process2.add(assembling);
        process2.add(coating);
        process2.add(assemblingAll);

        LinkedList<Step> process3 = new LinkedList<>();
        process3.add(drawerCutting);
        process3.add(drawerAssembling);
        process3.add(drawerPainting);
        process3.add(assemblingAll);

        List<LinkedList<Step>> processes = new ArrayList<>();
        processes.add(process1);
        processes.add(process2);
        processes.add(process3);

        return processes;

    }


    private static class Step {

        private String name;
        private BigDecimal hours;

        public Step(String name, BigDecimal hours) {
            this.name = name;
            this.hours = hours;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public BigDecimal getHours() {
            return hours;
        }

        public void setHours(BigDecimal hours) {
            this.hours = hours;
        }
    }


}
