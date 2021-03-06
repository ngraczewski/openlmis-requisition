package org.openlmis.fulfillment.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import org.hibernate.annotations.Type;
import org.openlmis.fulfillment.utils.LocalDateTimePersistenceConverter;
import org.openlmis.requisition.domain.BaseEntity;
import org.openlmis.requisition.domain.Requisition;
import org.openlmis.requisition.domain.RequisitionLineItem;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;

@Entity
@Table(name = "orders")
@NoArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Order extends BaseEntity {

  private static final String UUID = "pg-uuid";

  @OneToOne
  @JoinColumn(name = "requisitionId")
  @Getter
  @Setter
  private Requisition requisition;

  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @Convert(converter = LocalDateTimePersistenceConverter.class)
  @Getter
  @Setter
  private LocalDateTime createdDate;

  @Getter
  @Setter
  @Type(type = UUID)
  private UUID createdById;

  @Getter
  @Setter
  @Type(type = UUID)
  private UUID programId;

  @Getter
  @Setter
  @Type(type = UUID)
  private UUID requestingFacilityId;

  @Getter
  @Setter
  @Type(type = UUID)
  private UUID receivingFacilityId;

  @Getter
  @Setter
  @Type(type = UUID)
  private UUID supplyingFacilityId;

  @Column(nullable = false, unique = true, columnDefinition = "text")
  @Getter
  @Setter
  private String orderCode;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  @Getter
  @Setter
  private OrderStatus status;

  @Column(nullable = false)
  @Getter
  @Setter
  private BigDecimal quotedCost;

  @OneToMany(
      mappedBy = "order",
      cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REMOVE},
      fetch = FetchType.EAGER,
      orphanRemoval = true)
  @Getter
  @Setter
  private List<OrderLineItem> orderLineItems;

  /**
   * Static factory method for constructing new Order based on Requisition.
   * @param requisition Requisition to create instance from.
   */
  public static Order newOrder(Requisition requisition) {
    Order order = new Order();
    order.setRequisition(requisition);
    order.setStatus(OrderStatus.ORDERED);
    order.setQuotedCost(BigDecimal.ZERO);

    order.setReceivingFacilityId(requisition.getFacilityId());
    order.setRequestingFacilityId(requisition.getFacilityId());

    order.setSupplyingFacilityId(requisition.getSupplyingFacilityId());
    order.setProgramId(requisition.getProgramId());

    List<OrderLineItem> orderLineItems = new ArrayList<>();

    for (RequisitionLineItem lineItem : requisition.getRequisitionLineItems()) {
      orderLineItems.add(OrderLineItem.newOrderLineItem(lineItem, order));
    }

    order.setOrderLineItems(orderLineItems);

    return order;
  }

  @PrePersist
  private void prePersist() {
    this.createdDate = LocalDateTime.now();
  }

  /**
   * Copy values of attributes into new or updated Order.
   *
   * @param order Order with new values.
   */
  public void updateFrom(Order order) {
    this.requisition = order.requisition;
    this.createdById = order.createdById;
    this.programId = order.programId;
    this.requestingFacilityId = order.requestingFacilityId;
    this.receivingFacilityId = order.receivingFacilityId;
    this.supplyingFacilityId = order.supplyingFacilityId;
    this.orderCode = order.orderCode;
    this.status = order.status;
    this.quotedCost = order.quotedCost;
  }
}
