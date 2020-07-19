package com.budai.dsschallenge.ordering;

import com.budai.dsschallenge.data.BikeFactory;
import com.budai.dsschallenge.dto.CalculationOutput;
import com.budai.dsschallenge.dto.Order;
import com.budai.dsschallenge.dto.OrderOutput;
import com.budai.dsschallenge.dto.Program;
import com.budai.dsschallenge.service.DateUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractOrderingStrategy implements OrderingStrategy {

    protected CalculationOutput processOrdersInOrder(List<Order> orders, LocalDateTime currentDate, DateUtil dateUtil) {
        Machines machines = new Machines();
        List<OrderOutput> orderOutputs = new ArrayList<>();
        for (Order order : orders) {
            TaskTime result = machines.scheduleAllTaskInOrder(order.getId(), order.getQuantity(), BikeFactory.createBike(order.getCode()).getJobLengths());
            int profit = order.getProfit() * order.getQuantity();
            LocalDateTime started = dateUtil.getDateFromMinutes(result.getStartTime(), currentDate);
            LocalDateTime finished = dateUtil.getDateFromMinutes(result.getEndTime(), currentDate);
            int fine = order.getFine() * dateUtil.getOverdue(finished, order.getDeadline());
            OrderOutput orderOutput = new OrderOutput(order.getId(), profit - fine, fine, started, finished, order.getDeadline());
            orderOutputs.add(orderOutput);
        }


        return new CalculationOutput(orderOutputs, new ArrayList<Program>());
    }
}