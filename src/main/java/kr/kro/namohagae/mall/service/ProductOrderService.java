package kr.kro.namohagae.mall.service;

import kr.kro.namohagae.mall.dao.*;
import kr.kro.namohagae.mall.dto.AddressDto;
import kr.kro.namohagae.mall.dto.ProductDto;
import kr.kro.namohagae.mall.dto.ProductOrderDto;
import kr.kro.namohagae.mall.entity.Address;
import kr.kro.namohagae.mall.entity.CartDetail;
import kr.kro.namohagae.mall.entity.ProductOrder;
import kr.kro.namohagae.mall.entity.ProductOrderDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;


@RequiredArgsConstructor
@Service
public class ProductOrderService {
    private final ProductOrderDao productOrderDao;
    private final ProductOrderDetailDao productOrderDetailDao;
    private final CartDetailDao cartDetailDao;
    private final AddressDao addressDao;
    private final ProductDao productDao;

    // 결제할 주문 목록 조회(장바구니)
    public ProductOrderDto.Read orderReady(Integer memberNo, List<Integer> checkedProductNos, Integer productOrderDetailCount) {
        // 선택한 상품만 장바구니에 담기
        List<ProductOrderDto.OrderList> orderItems = new ArrayList<>();
        Integer orderTotalPrice = 0;
        for (Integer productNo : checkedProductNos) {
            Optional<CartDetail> result = cartDetailDao.findByMemberNoAndProductNo(memberNo, productNo);
            CartDetail cartDetail;
            ProductDto.Read product = productDao.findByProductNo(productNo);
            if (result.isPresent()) {
                cartDetail = result.get();
            } else{
                cartDetail = new CartDetail(null,memberNo,null,productNo,productOrderDetailCount,product.getProductPrice());
            }
                ProductOrderDto.OrderList orderItem = new ProductOrderDto.OrderList(cartDetail.getProductNo(), product.getProductImages().get(0), product.getProductName(), cartDetail.getCartDetailCount(), cartDetail.getCartDetailPrice(), cartDetail.getCartDetailCount()*product.getProductPrice());
                orderItems.add(orderItem);
                orderTotalPrice += orderItem.getOrderTotalPrice();
        }

        return new ProductOrderDto.Read(orderItems, orderTotalPrice);
    }


    // 멤버의 주소 찾기
    public List<AddressDto.Read> findAddress(Integer memberNo) {
        return addressDao.findAll(memberNo);
    }
    // 아래부터 수정해야함 (dto 수정했고, 매퍼 수정)

    public List<ProductOrderDto.OrderResult> orderList(Integer memberNo) {
        return productOrderDao.list(memberNo);
    }

    public ProductOrderDto.OrderResult findById(Integer orderNo) {
        return productOrderDao.read(orderNo);
    }

    public Integer saveOrder(List<ProductOrderDto.OrderList> orderItems, Integer orderTotalPrice, Integer memberNo, Integer addressNo) {
        Address address = addressDao.findByMemberNoAndAddressNo(memberNo, addressNo);
        ProductOrder productOrder = new ProductOrder(null, memberNo, address.getAddressNo(), orderTotalPrice, LocalDateTime.now());
        productOrderDao.save(productOrder);
        Integer productOrderNo = productOrder.getProductOrderNo();
        List<Integer> productNos = new ArrayList<>();
        for (ProductOrderDto.OrderList item:orderItems) {
            ProductOrderDetail productOrderDetail = ProductOrderDetail.builder().productOrderNo(productOrderNo).productNo(item.getProductNo()).productOrderDetailCount(item.getCartDetailCount()).productOrderDetailPrice(item.getCartDetailPrice()).build();
            productOrderDetailDao.save(productOrderDetail);

            // 상품 번호 추출
            Integer productNo = item.getProductNo();
            productNos.add(productNo);

            // 상품 재고 처리
            Integer productStock = productDao.findInformationByProductNo(productNo);
            if (productStock >= item.getCartDetailCount()) {
                Map<String, Object> params = new HashMap<>();
                params.put("productNo", productNo);
                params.put("productOrderDetailNo", productOrderDetail.getProductOrderDetailNo());
                productDao.updateStockByProductNo(params);
            } else {
                throw new RuntimeException("재고가 부족합니다.");
            }
        }
        if (!productNos.isEmpty()) {
            cartDetailDao.removeByCartNo(productNos, memberNo);
        }
        return productOrder.getProductOrderNo();

    }
}