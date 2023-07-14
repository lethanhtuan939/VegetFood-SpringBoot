package vn.LeThanhTuan.entity.id;

import java.io.Serializable;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailId implements Serializable {

	private Integer order;
	private Integer product;

	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof OrderDetailId))
			return false;
		OrderDetailId that = (OrderDetailId) o;
		return Objects.equals(getOrder(), that.getOrder()) && Objects.equals(getProduct(), that.getProduct());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getOrder(), getProduct());
	}
}
