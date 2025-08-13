package javagymrat.paymentservice.mapper;

import javagymrat.paymentservice.model.dto.CancelPaymentRequest;
import javagymrat.paymentservice.model.dto.CancelPaymentResponse;
import javagymrat.paymentservice.model.dto.enums.CommandResultStatus;
import javagymrat.paymentservice.model.entity.Refund;
import javagymrat.paymentservice.model.enums.RefundStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface RefundMapper {

    Refund toEntity(CancelPaymentRequest cancelPaymentRequest,
                    RefundStatus status);

    @Mapping(source = "status", target = "status", qualifiedByName = "mapRefundStatusToCommandStatus")
    CancelPaymentResponse toResponse(Refund refund);

    @Named("mapRefundStatusToCommandStatus")
    default CommandResultStatus mapRefundStatusToCommandStatus(RefundStatus refundStatus) {
        if (refundStatus == null) {
            return CommandResultStatus.FAILED;
        }
        return switch (refundStatus) {
            case COMPLETED -> CommandResultStatus.SUCCESS;
            case FAILED -> CommandResultStatus.FAILED;
        };
    }
}
