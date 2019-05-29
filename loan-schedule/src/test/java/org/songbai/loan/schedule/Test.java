//package org.songbai.exchange.schedule;
//
//import org.quartz.CronExpression;
//import org.quartz.impl.calendar.CronCalendar;
//import org.quartz.impl.triggers.CronTriggerImpl;
//import org.springframework.scheduling.support.CronTrigger;
//import org.springframework.scheduling.support.SimpleTriggerContext;
//
//import java.text.ParseException;
//import java.util.Date;
//
//public class Test {
//
//    public static void main(String[] args) throws ParseException {
////        CronTrigger cronTrigger = new CronTrigger(");
//
//
//        CronTriggerImpl impl = new CronTriggerImpl();
//
//        impl.setCronExpression(new CronExpression("0 0 2 * * ?"));
//
//
//        System.out.println(impl.getStartTime());
//        System.out.println(impl.getPreviousFireTime());
////        System.out.println(impl.computeFirstFireTime());
//
//        System.out.println(new Date(        new CronCalendar("0 0 2 * * ?").getNextIncludedTime()));
//    }
//}
