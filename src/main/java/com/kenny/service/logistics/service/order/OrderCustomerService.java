package com.kenny.service.logistics.service.order;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kenny.service.logistics.exception.OrderErrorCode;
import com.kenny.service.logistics.mapper.order.OrderGoodsMapper;
import com.kenny.service.logistics.mapper.order.OrderStatusMapper;
import com.kenny.service.logistics.model.order.OrderGoods;
import com.kenny.service.logistics.model.order.OrderStatus;
import com.kenny.service.logistics.model.system.Defind;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Date;
import java.util.ArrayList;
import java.util.Random;

import com.kenny.service.logistics.json.response.PageResponse;
import com.kenny.service.logistics.exception.ErrorCodeException;
import com.kenny.service.logistics.model.order.OrderCustomer;
import com.kenny.service.logistics.mapper.order.OrderCustomerMapper;

@Service
public class OrderCustomerService {
    @Autowired
    private OrderCustomerMapper orderCustomerMapper;
    @Autowired
    private OrderGoodsMapper orderGoodsMapper;

    //创建订单
    public OrderCustomer insert(String send_name,
                                String send_phone,
                                String send_addr,
                                String send_addr_info,
                                String recive_name,
                                String recive_phone,
                                String recive_addr,
                                String recive_addr_info,
                                String dispatching_type,
                                Date send_time,
                                Date recive_time,
                                Integer user_id,
                                Float freight,
                                Float safes,
                                String goods) {
        OrderCustomer orderCustomer = new OrderCustomer();
        //流水号
        orderCustomer.setSerial_number(createSerialNumber());
        //单号
        orderCustomer.setOrder_number(createOrderNumber());
        orderCustomer.setSend_name(send_name);
        orderCustomer.setSend_addr(send_addr);
        orderCustomer.setSend_addr_info(send_addr_info);
        orderCustomer.setSend_phone(send_phone);
        orderCustomer.setSend_time(send_time);
        orderCustomer.setRecive_name(recive_name);
        orderCustomer.setRecive_phone(recive_phone);
        orderCustomer.setRecive_addr(recive_addr);
        orderCustomer.setRecive_addr_info(recive_addr_info);
        orderCustomer.setRecive_time(recive_time);
        orderCustomer.setDispatching_type(dispatching_type);
        orderCustomer.setTime(new Date());
        orderCustomer.setFk_user_id(user_id);
        orderCustomer.setFreight(freight);
        orderCustomer.setSafes(safes);
        orderCustomer.setTotal(freight + safes);
        orderCustomerMapper.insert(orderCustomer);

        try {
            if (goods != null) {
                goods = "[" + goods + "]";
                Gson gson = new Gson();
                List<OrderGoods> goodsList = gson.fromJson(goods, new TypeToken<List<OrderGoods>>() {
                }.getType());
                //添加货物
                for (OrderGoods good : goodsList) {
                    good.setFk_order_customer_id(orderCustomer.getId());
                    orderGoodsMapper.insert(good);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println(goods);
        }
        return orderCustomer;
    }

    //修改订单
    public OrderCustomer update(Integer id,
                                String send_name,
                                String send_phone,
                                String send_addr,
                                String send_addr_info,
                                String recive_name,
                                String recive_phone,
                                String recive_addr,
                                String recive_addr_info,
                                String dispatching_type,
                                Date send_time,
                                Date recive_time,
                                String goods) throws ErrorCodeException {
        OrderCustomer orderCustomer = orderCustomerMapper.selectByPrimaryKey(id);
        if (orderCustomer == null) {
            throw new ErrorCodeException(ErrorCodeException.DATA_NO_ERROR);
        }
        orderCustomer.setSend_name(send_name);
        orderCustomer.setSend_phone(send_phone);
        orderCustomer.setSend_addr(send_addr);
        orderCustomer.setSend_addr_info(send_addr_info);
        orderCustomer.setRecive_name(recive_name);
        orderCustomer.setRecive_phone(recive_phone);
        orderCustomer.setRecive_addr(recive_addr);
        orderCustomer.setRecive_addr_info(recive_addr_info);
        orderCustomer.setSend_time(send_time);
        orderCustomer.setRecive_time(recive_time);
        orderCustomer.setDispatching_type(dispatching_type);
        orderCustomerMapper.update(orderCustomer);

        //货物修改
        //删除所有这个订单的货物
        orderGoodsMapper.deleteByOrderCustomer(id);
        if (goods != null) {
            Gson gson = new Gson();
            List<OrderGoods> goodsList = gson.fromJson(goods, new TypeToken<List<OrderGoods>>() {
            }.getType());
            for (OrderGoods good : goodsList) {
                good.setFk_order_customer_id(orderCustomer.getId());
                orderGoodsMapper.insert(good);
            }
        }
        return orderCustomer;
    }

    //修改订单
    public OrderCustomer updateStatus(Integer id, String status) throws ErrorCodeException {
        OrderCustomer orderCustomer = orderCustomerMapper.selectByPrimaryKey(id);
        if (orderCustomer == null) {
            throw new ErrorCodeException(ErrorCodeException.DATA_NO_ERROR);
        }
        orderCustomer.setStatus(status);
        orderCustomerMapper.update(orderCustomer);
        return orderCustomer;
    }

    public OrderCustomer selectByPrimaryKey(Integer id) throws ErrorCodeException {
        OrderCustomer orderCustomer = orderCustomerMapper.selectByPrimaryKey(id);
        if (orderCustomer == null) {
            throw new ErrorCodeException(ErrorCodeException.DATA_NO_ERROR);
        }
        return orderCustomer;
    }

    public PageResponse<OrderCustomer> selectPage(Integer offset, Integer pageSize) {
        PageResponse<OrderCustomer> response = new PageResponse();
        response.setItem(orderCustomerMapper.selectPage(offset, pageSize));
        response.setTotal(orderCustomerMapper.count());
        response.setOffset(offset);
        response.setPageSize(pageSize);
        return response;
    }

    public int deleteByPrimaryKey(Integer id){
        return orderCustomerMapper.deleteByPrimaryKey(id);
    }

    /**
     * 随机创建订单流水号
     *
     * @return
     */
    private String createSerialNumber() {
        Random random = new Random(System.currentTimeMillis());
        String number = new Date().getTime() + "";
        for (int i = 0; i < 5; i++)
            number = number + random.nextInt(9);
        return number;
    }

    /**
     * 随机创建订单号
     *
     * @return
     */
    private String createOrderNumber() {
        Random random = new Random(System.currentTimeMillis());
        SimpleDateFormat myFmt = new SimpleDateFormat("yyyyMMddHHmm");
        String number = myFmt.format(new Date());
        for (int i = 0; i < 5; i++)
            number = number + random.nextInt(9);
        return number;
    }
}
